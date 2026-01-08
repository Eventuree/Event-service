package eventure.event_service.Service.ServiceImpl;

import eventure.event_service.Model.Entity.Event;
import eventure.event_service.Repository.EventRepository;
import eventure.event_service.Service.EventService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventRepository jpaRepository;

    public EventServiceImpl(EventRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Event> getAll() {
          return List.of();
    }
}
