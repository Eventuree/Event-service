package eventure.event_service.service.serviceImpl;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventPageResponse;
import eventure.event_service.dto.EventUpdateDto;
import eventure.event_service.dto.mapper.EventMapper;
import eventure.event_service.exception.ResourceNotFoundException;
import eventure.event_service.model.EventStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.EventCategory;
import eventure.event_service.repository.EventCategoryRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.service.EventService;
import eventure.event_service.service.imageStorage.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final ImageService imageService;
    private final TransactionTemplate transactionTemplate;

    @Override
    public List<Event> getTrendingEvents() {
        return eventRepository.findTop10ByOrderByViewCountDesc();
    }

    @Override
    @Transactional
    public Event getEventById(Long id) {
        eventRepository.incrementViewCount(id);

        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
    }

    @Override
    public Event createEvent(EventCreateDto eventDto) {
        String imageUrl = (eventDto.getPhoto() != null && !eventDto.getPhoto().isBlank())
                ? imageService.uploadBase64Image(eventDto.getPhoto()).join()
                : null;

        return transactionTemplate.execute(status -> {
            Event event = eventMapper.toEntityCreate(eventDto);

            EventCategory category = categoryRepository.findById(eventDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

            event.setCategory(category);
            event.setBannerPhotoUrl(imageUrl);
            event.setStatus(EventStatus.PUBLISHED);

            return eventRepository.save(event);
        });
    }

    @Override
    public Event updateEventById(Long id, EventUpdateDto eventDto) {
        String newImageUrl = (eventDto.getPhoto() != null && !eventDto.getPhoto().isBlank())
                ? imageService.uploadBase64Image(eventDto.getPhoto()).join()
                : null;

        return transactionTemplate.execute(status -> {
            Event existingEvent = eventRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

            LocalDateTime originalDate = existingEvent.getEventDate();

            eventMapper.updateEntityFromDto(eventDto, existingEvent);

            if (existingEvent.getEventDate().isAfter(originalDate)) {
                existingEvent.setStatus(EventStatus.DELAYED);
            }

            if (eventDto.getCategoryId() != null) {
                EventCategory category = categoryRepository.findById(eventDto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
                existingEvent.setCategory(category);
            }

            if (newImageUrl != null) {
                existingEvent.setBannerPhotoUrl(newImageUrl);
            }

            return eventRepository.save(existingEvent);
        });
    }

    @Override
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    @Override
    public EventPageResponse getAllEventsPagination(int pageNo, int limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);

        Page<Event> page = eventRepository.findAll(pageable);

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
    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }
}
