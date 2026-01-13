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
import eventure.event_service.service.aws.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final ImageService imageService;

    @Override
    public List<Event> getTrendingEvents() {
        return eventRepository.findTop10ByOrderByViewCountDesc();
    }

    @Override
    @Transactional
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        event.setViewCount(event.getViewCount() + 1);
        return eventRepository.save(event);
    }

    @Transactional
    @Override
    public Event createEvent(EventCreateDto eventDto) {
        Event event = eventMapper.toEntityCreate(eventDto);

        EventCategory category = categoryRepository.findById(eventDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + eventDto.getCategoryId()));
        event.setCategory(category);

        String imageUrl = imageService.uploadBase64Image(eventDto.getPhoto());
        event.setBannerPhotoUrl(imageUrl);

        event.setStatus(EventStatus.PUBLISHED);

        return eventRepository.save(event);
    }

    @Transactional
    @Override
    public Event updateEventById(Long id, EventUpdateDto eventDto) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        LocalDateTime existingEventDate = existingEvent.getEventDate();
        eventMapper.updateEntityFromDto(eventDto, existingEvent);

        if (existingEventDate.isBefore(existingEvent.getEventDate())){
            existingEvent.setStatus(EventStatus.DELAYED);
        }

        if (eventDto.getCategoryId() != null) {
            EventCategory category = categoryRepository.findById(eventDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            existingEvent.setCategory(category);
        }

        String imageUrl = imageService.uploadBase64Image(eventDto.getPhoto());
        existingEvent.setBannerPhotoUrl(imageUrl);

        return eventRepository.save(existingEvent);
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
