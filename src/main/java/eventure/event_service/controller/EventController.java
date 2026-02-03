package eventure.event_service.controller;

import eventure.event_service.dto.*;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.service.EventParticipantService;
import eventure.event_service.service.EventService;
import eventure.event_service.utils.SecurityHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventParticipantService participantService;
    private final SecurityHelper securityHelper;

    @GetMapping("/trending")
    public ResponseEntity<List<EventResponseDto>> getTrendingEvents() {
        return ResponseEntity.ok(eventService.getTrendingEvents());
    }

    @GetMapping("")
    public ResponseEntity<EventPageResponse> getPaginatedEvents(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            EventFiltersDto eventFilters) {
        return ResponseEntity.ok(eventService.getAllEventsPagination(page, limit, eventFilters));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> updateEventById(
            @PathVariable Long id,
            @RequestPart EventUpdateDto eventDto,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            HttpServletRequest request) {
        Long currentUserId = participantService.extractUserId(request);

        return ResponseEntity.ok(eventService.updateEventById(id, eventDto, photo, currentUserId));
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
    public ResponseEntity<?> deleteEventById(@PathVariable Long id,
                                             HttpServletRequest request) {
        Long currentUserId = participantService.extractUserId(request);
        eventService.deleteEventById(id, currentUserId);
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

    @PostMapping("/{id}/register")
    public ResponseEntity<EventParticipantDto> registerForEvent(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = participantService.extractUserId(request);
        EventParticipantDto participant = participantService.registerParticipant(id, userId);
        return ResponseEntity.created(URI.create("/api/v1/events/" + id + "/participants"))
                .body(participant);
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = participantService.extractUserId(request);
        participantService.cancelRegistration(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDto>> getUserEvents(
            @PathVariable Long userId) {

        return ResponseEntity.ok(eventService.getUserEvents(userId));
    }

    @GetMapping("/archive")
    public ResponseEntity<Page<EventResponseDto>> getArchivedEvents(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request
    ) {
        Long userId = securityHelper.extractUserId(request);

        return ResponseEntity.ok(eventService.getArchivedEvents(userId, type, page, limit));
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<EventResponseDto>> getUserRegistrations(
            @RequestParam RegistrationStatus status,
            HttpServletRequest request) {
        Long userId = participantService.extractUserId(request);

        return ResponseEntity.ok(eventService.getUserEventsByStatus(userId, status));
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventResponseDto>> getMyEvents(
            @RequestParam(required = false, defaultValue = "APPROVED") RegistrationStatus status,
            HttpServletRequest request) {
        Long userId = participantService.extractUserId(request);

        return ResponseEntity.ok(eventService.getMyEvents(userId, status));
    }

}
