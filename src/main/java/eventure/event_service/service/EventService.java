package eventure.event_service.service;

import eventure.event_service.dto.*;
import eventure.event_service.model.entity.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    List<EventResponseDto> getTrendingEvents();

    EventPageResponse getAllEventsPagination(int page, int limit, EventFiltersDto eventFilters);

    EventResponseDto getEventById(Long id);

    EventResponseDto createEvent(EventCreateDto eventDto, MultipartFile photo);

    EventResponseDto updateEventById(Long id, EventUpdateDto eventDto, MultipartFile photo);

    void deleteEventById(Long id);
}
