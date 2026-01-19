package eventure.event_service.service.serviceImpl;

import eventure.event_service.client.ProfileServiceClient;
import eventure.event_service.dto.EventParticipantDto;
import eventure.event_service.dto.UserProfileDto;
import eventure.event_service.exception.AccessDeniedException;
import eventure.event_service.exception.EventCapacityExceededException;
import eventure.event_service.exception.ResourceNotFoundException;
import eventure.event_service.exception.UnauthorizedException;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.model.entity.EventParticipantId;
import eventure.event_service.repository.EventParticipantRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.service.EventParticipantService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ProfileServiceClient profileServiceClient;

    public List<EventParticipantDto> getParticipants(Long eventId, Long currentUserId) {
        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getOrganizerId().equals(currentUserId)) {
            throw new AccessDeniedException("Access denied. Only organizer can view participants.");
        }

        List<EventParticipant> participants = participantRepository.findById_EventId(eventId);

        return participants.stream()
                .map(
                        p -> {
                            UserProfileDto profile = null;
                            try {
                                profile =
                                        profileServiceClient.getUserProfile(p.getId().getUserId());
                            } catch (Exception e) {
                                profile = new UserProfileDto("Unknown User", null);
                            }

                            return EventParticipantDto.builder()
                                    .userId(p.getId().getUserId())
                                    .status(p.getStatus())
                                    .joinedAt(p.getJoinedAt())
                                    .name(profile.getName())
                                    .avatarUrl(profile.getAvatarUrl())
                                    .build();
                        })
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeStatus(
            Long eventId, Long participantId, RegistrationStatus newStatus, Long currentUserId) {
        Event event = getEventAndValidateOrganizer(eventId, currentUserId);

        EventParticipant participant = getParticipantOrThrow(participantId, eventId);

        if (newStatus == RegistrationStatus.APPROVED) {
            validateEventCapacity(event);
        }

        participant.setStatus(newStatus);
        participantRepository.save(participant);
    }

    public Long extractUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr instanceof Long userId) {
            return userId;
        }

        if (userIdAttr instanceof Number num) {
            return num.longValue();
        }

        if (userIdAttr instanceof String str && !str.isBlank()) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ignored) {
            }
        }

        throw new UnauthorizedException(
                "Authenticated user id is not present in request attributes");
    }

    private Event getEventAndValidateOrganizer(Long eventId, Long currentUserId) {
        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (!event.getOrganizerId().equals(currentUserId)) {
            throw new AccessDeniedException("Access denied. Only organizer can manage requests.");
        }
        return event;
    }

    private EventParticipant getParticipantOrThrow(Long participantId, Long eventId) {
        return participantRepository
                .findById(new EventParticipantId(participantId, eventId))
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
    }

    private void validateEventCapacity(Event event) {
        long approvedCount =
                participantRepository.countById_EventIdAndStatus(
                        event.getId(), RegistrationStatus.APPROVED);

        if (event.getMaxParticipants() != null && approvedCount >= event.getMaxParticipants()) {
            throw new EventCapacityExceededException("Event is full.");
        }
    }
}
