package eventure.event_service.service;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventPageResponse;
import eventure.event_service.dto.EventResponseDto;
import eventure.event_service.dto.EventUpdateDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {

    List<EventResponseDto> getTrendingEvents();

    List<EventResponseDto> getAllEvents();

    EventPageResponse getAllEventsPagination(int page, int limit);

    EventResponseDto getEventById(Long id);

    EventResponseDto createEvent(EventCreateDto eventDto, MultipartFile photo);

    EventResponseDto updateEventById(Long id, EventUpdateDto eventDto, MultipartFile photo);

    void deleteEventById(Long id);
}
