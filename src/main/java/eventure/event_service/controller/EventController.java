package eventure.event_service.controller;

import eventure.event_service.dto.*;
import eventure.event_service.service.EventParticipantService;
import eventure.event_service.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventParticipantService participantService;

    @GetMapping("/trending")
    public ResponseEntity<List<EventResponseDto>> getTrendingEvents() {
        return ResponseEntity.ok(eventService.getTrendingEvents());
    }

    @GetMapping("")
    public ResponseEntity<EventPageResponse> getPaginatedEvents(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(eventService.getAllEventsPagination(page, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEventById(
            @PathVariable Long id,
            @RequestPart EventUpdateDto eventDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        return ResponseEntity.ok(eventService.updateEventById(id, eventDto, photo));
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(
            @RequestPart("event") @Valid EventCreateDto eventDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        EventResponseDto createdEvent = eventService.createEvent(eventDto, photo);
        return ResponseEntity.created(URI.create("/api/v1/events/" + createdEvent.getId()))
                .body(createdEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventById(@PathVariable Long id) {
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<EventParticipantDto>> getParticipants(
            @PathVariable Long eventId, HttpServletRequest request) {
        Long currentUserId = participantService.extractUserId(request);

        return ResponseEntity.ok(participantService.getParticipants(eventId, currentUserId));
    }

    @PutMapping("/{eventId}/participants/{userId}")
    public ResponseEntity<Void> changeParticipantStatus(
            @PathVariable Long eventId,
            @PathVariable Long userId,
            @RequestBody UpdateStatusRequest request,
            HttpServletRequest httpRequest) {
        Long currentUserId = participantService.extractUserId(httpRequest);

        participantService.changeStatus(eventId, userId, request.getStatus(), currentUserId);
        return ResponseEntity.ok().build();
    }
}
