package eventure.event_service.service;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventPageResponse;
import eventure.event_service.dto.EventUpdateDto;
import eventure.event_service.model.entity.Event;
import java.util.List;

public interface EventService {

    List<Event> getTrendingEvents();

    List<Event> getAllEvents();

    EventPageResponse getAllEventsPagination(int page, int limit);

    Event getEventById(Long id);

    Event createEvent(EventCreateDto eventDto);

    Event updateEventById(Long id, EventUpdateDto eventDto);

    void deleteEventById(Long id);
}