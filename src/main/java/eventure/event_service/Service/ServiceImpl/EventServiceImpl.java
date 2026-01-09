package eventure.event_service.Service.ServiceImpl;

import eventure.event_service.Model.Entity.Event;
import eventure.event_service.Repository.EventRepository;
import eventure.event_service.Service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<Event> getTrendingEvents() {
        return eventRepository.findTop10ByOrderByViewCountDesc();
    }

    @Override
    @Transactional
    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        event.setViewCount(event.getViewCount() + 1);
        return eventRepository.save(event);
    }

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }
}
