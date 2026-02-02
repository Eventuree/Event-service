package eventure.event_service.service.serviceImpl;

import eventure.event_service.dto.*;
import eventure.event_service.dto.mapper.EventMapper;
import eventure.event_service.exception.ForbiddenException;
import eventure.event_service.exception.ResourceNotFoundException;
import eventure.event_service.model.EventStatus;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.EventCategory;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.repository.EventCategoryRepository;
import eventure.event_service.repository.EventParticipantRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.service.EventParticipantService;
import eventure.event_service.service.EventService;
import eventure.event_service.service.imageStorage.ImageService;
import eventure.event_service.utils.EventSpecificationsUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final ImageService imageService;
    private final TransactionTemplate transactionTemplate;
    private final EventParticipantRepository eventParticipantRepository;

    @Override
    public List<EventResponseDto> getTrendingEvents() {
        return eventRepository.findTop10ByOrderByViewCountDesc().stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventResponseDto getEventById(Long id) {
        eventRepository.incrementViewCount(id);

        return eventMapper.toDto(
                eventRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Event not found with id: " + id)));
    }

    @Override
    public EventResponseDto createEvent(EventCreateDto eventDto, MultipartFile photo) {
        final String imageUrl =
                (photo != null && !photo.isEmpty()) ? imageService.uploadImage(photo).join() : null;

        Event savedEvent =
                transactionTemplate.execute(
                        status -> {
                            Event event = eventMapper.toEntityCreate(eventDto);

                            EventCategory category =
                                    categoryRepository
                                            .findById(eventDto.getCategoryId())
                                            .orElseThrow(
                                                    () ->
                                                            new ResourceNotFoundException(
                                                                    "Category not found"));

                            event.setCategory(category);
                            event.setBannerPhotoUrl(imageUrl);
                            event.setStatus(EventStatus.PUBLISHED);

                            return eventRepository.save(event);
                        });

        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventResponseDto updateEventById(Long id, EventUpdateDto eventDto, MultipartFile photo, Long currentUserId) {

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!existingEvent.getOrganizerId().equals(currentUserId)) {
            throw new ForbiddenException("Only organizer can edit this event");
        }

        final String imageUrl =
                (photo != null && !photo.isEmpty()) ? imageService.uploadImage(photo).join() : null;

        Event savedEvent =
                transactionTemplate.execute(
                        status -> {

                            LocalDateTime originalDate = existingEvent.getEventDate();

                            eventMapper.updateEntityFromDto(eventDto, existingEvent);

                            if (existingEvent.getEventDate().isAfter(originalDate)) {
                                existingEvent.setStatus(EventStatus.DELAYED);
                            }

                            if (eventDto.getCategoryId() != null) {
                                EventCategory category =
                                        categoryRepository
                                                .findById(eventDto.getCategoryId())
                                                .orElseThrow(
                                                        () ->
                                                                new ResourceNotFoundException(
                                                                        "Category not found"));
                                existingEvent.setCategory(category);
                            }

                            if (imageUrl != null) {
                                existingEvent.setBannerPhotoUrl(imageUrl);
                            }

                            return eventRepository.save(existingEvent);
                        });

        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventPageResponse getAllEventsPagination(int pageNo,
                                                    int limit,
                                                    EventFiltersDto eventFilters) {
        Pageable pageable = PageRequest.of(pageNo, limit);
        Specification<Event> eventSpecifications =
                EventSpecificationsUtils.buildFilters(eventFilters);

        Page<Event> page = eventRepository.findAll(eventSpecifications, pageable);

        List<Event> content = page.getContent();

        EventPageResponse eventPage = new EventPageResponse();
        eventPage.setContent(content);
        eventPage.setPageNo(page.getNumber());
        eventPage.setPageSize(page.getSize());
        eventPage.setTotalElements(page.getTotalElements());
        eventPage.setTotalPages(page.getTotalPages());
        eventPage.setLast(page.isLast());

        return eventPage;
    }

    @Override
    public void deleteEventById(Long id, Long currentUserId) {

        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!existingEvent.getOrganizerId().equals(currentUserId)) {
            throw new ForbiddenException("Only organizer can delete this event");
        }

        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponseDto> getUserEvents(Long userId) {
        List<Event> events = eventRepository.findByOrganizerId(userId);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    public Page<EventResponseDto> getArchivedEvents(Long userId, String type, int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("eventDate").descending());
        LocalDateTime now = LocalDateTime.now();

        Page<Event> events;

        if (type == null || type.isBlank()) {
            events = eventRepository.findAllArchivedEvents(userId, now, pageable);

        } else if ("created".equalsIgnoreCase(type)) {
            events = eventRepository.findAllByOrganizerIdAndEventDateBefore(userId, now, pageable);

        } else if ("attended".equalsIgnoreCase(type)) {
            events = eventRepository.findAttendedEvents(userId, now, pageable);

        } else {
            throw new IllegalArgumentException("Invalid archive type. Use 'created', 'attended' or leave empty.");
        }

        return events.map(eventMapper::toDto);
    }

    @Override
    public List<EventResponseDto> getUserEventsByStatus(Long userId, RegistrationStatus status) {
        List<EventParticipant> participants = eventParticipantRepository.findByUserIdAndStatus(userId, status);

        return participants.stream()
                .map(EventParticipant::getEvent)
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDto> getMyEvents(Long userId, RegistrationStatus status) {
        List<EventResponseDto> createdEvents = getUserEvents(userId);
        List<EventResponseDto> registeredEvents = getUserEventsByStatus(userId, status);

        return Stream.concat(createdEvents.stream(), registeredEvents.stream())
                .collect(Collectors.toList());
    }

}
