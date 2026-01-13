package eventure.event_service.controller;

import eventure.event_service.dto.EventCreateDto;
import eventure.event_service.dto.EventPageResponse;
import eventure.event_service.dto.EventUpdateDto;
import eventure.event_service.model.entity.Event;
import eventure.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/trending")
    public ResponseEntity<List<Event>> getTrendingEvents() {
        return ResponseEntity.ok(eventService.getTrendingEvents());
    }

    @GetMapping("")
    public ResponseEntity<EventPageResponse> getPaginatedEvents(@RequestParam(required = false, defaultValue = "0") Integer page,
                                                                @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(eventService.getAllEventsPagination(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEventById(@PathVariable Long id, @RequestBody EventUpdateDto eventDto){
        return ResponseEntity.ok(eventService.updateEventById(id, eventDto));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventCreateDto eventDto){
        Event createdEvent = eventService.createEvent(eventDto);
        return ResponseEntity.created(URI.create("/api/v1/events/" + createdEvent.getId())).body(createdEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventById(@PathVariable Long id){
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }
}
