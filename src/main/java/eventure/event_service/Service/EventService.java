package eventure.event_service.Service;

import eventure.event_service.Model.Entity.Event;

import java.util.List;

public interface EventService {
    List<Event> getAll();
}
