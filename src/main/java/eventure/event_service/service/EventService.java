package eventure.event_service.service;

import eventure.event_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EventService {

    List<EventResponseDto> getTrendingEvents();

    EventPageResponse getAllEventsPagination(int page, int limit, EventFiltersDto eventFilters);

    EventResponseDto getEventById(Long id);

    EventResponseDto createEvent(EventCreateDto eventDto, MultipartFile photo);

    EventResponseDto updateEventById(Long id, EventUpdateDto eventDto, MultipartFile photo, Long currentUserId);

    void deleteEventById(Long id, Long currentUserId);

    List<EventResponseDto> getUserEvents(Long userId);

    Page<EventResponseDto> getArchivedEvents(Long userId, String type, int page, int limit);
}
