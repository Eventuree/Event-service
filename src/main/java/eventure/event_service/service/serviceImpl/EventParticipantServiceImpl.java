package eventure.event_service.service.serviceImpl;

import eventure.event_service.client.ProfileServiceClient;
import eventure.event_service.dto.EventParticipantDto;
import eventure.event_service.dto.UserProfileDto;
import eventure.event_service.dto.UserProfileSummaryDto;
import eventure.event_service.exception.*;
import eventure.event_service.messaging.StatusNotificationPublisher;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.model.entity.EventParticipantId;
import eventure.event_service.repository.EventParticipantRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.service.EventParticipantService;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventParticipantServiceImpl implements EventParticipantService {

    private final EventParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ProfileServiceClient profileServiceClient;
    private final StatusNotificationPublisher statusPublisher;

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
                            UserProfileSummaryDto profile = null;
                            try {
                                profile =
                                        profileServiceClient.getUserProfileSummary(p.getId().getUserId());
                            } catch (Exception e) {
                                profile = new UserProfileSummaryDto("Unknown User", null, null);
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
    public void changeStatus(Long eventId, Long participantId, RegistrationStatus newStatus, Long currentUserId) {
        Event event = getEventAndValidateOrganizer(eventId, currentUserId);
        EventParticipant participant = getParticipantOrThrow(participantId, eventId);

        if (newStatus == RegistrationStatus.APPROVED) {
            validateEventCapacity(event);
        }

        participant.setStatus(newStatus);
        participantRepository.save(participant);

        try {
            UserProfileSummaryDto userProfile = profileServiceClient.getUserProfileSummary(participantId);

            if (userProfile != null && userProfile.getEmail() != null) {
                statusPublisher.sendStatusChangeNotification(
                        userProfile.getEmail(),
                        userProfile.getName(),
                        event.getTitle(),
                        newStatus.name()
                );
            }
        } catch (NotificationSendingException e) {
            log.warn("Status changed to {}, but notification FAILED: {}", newStatus, e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error during notification sending", e);
        }
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

    @Transactional
    public EventParticipantDto registerParticipant(Long eventId, Long userId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        EventParticipantId participantId = new EventParticipantId(userId, eventId);
        participantRepository.findById(participantId).ifPresent(existing -> {
            if (existing.getStatus() != RegistrationStatus.CANCELED &&
                    existing.getStatus() != RegistrationStatus.LEFT) {
                throw new DuplicateRegistrationException("User is already registered for this event");
            }
        });

        UserProfileDto userProfile = profileServiceClient.getUserProfile(userId);
        if (userProfile == null) {
            throw new ResourceNotFoundException("User profile not found");
        }

        if (event.getMinAge() != null && userProfile.getAge() != null) {
            if (userProfile.getAge() < event.getMinAge()) {
                throw new ValidationException("User does not meet minimum age requirement");
            }
        }

        if (event.getMaxAge() != null && userProfile.getAge() != null) {
            if (event.getMaxAge() < userProfile.getAge()) {
                throw new ValidationException("User does not meet maximum age requirement");
            }
        }

        if (event.getRequiredGender() != null && userProfile.getGender() != null) {
            if (!event.getRequiredGender().toString().equalsIgnoreCase(userProfile.getGender())) {
                throw new ValidationException("User does not meet gender requirement");
            }
        }

        if (event.getMaxParticipants() != null) {
            long currentCount = participantRepository.countById_EventIdAndStatus(
                    eventId, RegistrationStatus.APPROVED);

            if (currentCount >= event.getMaxParticipants()) {
                throw new EventCapacityExceededException("Event has reached maximum participants");
            }
        }

        EventParticipant participant = EventParticipant.builder()
                .id(participantId)
                .event(event)
                .status(RegistrationStatus.PENDING)
                .joinedAt(LocalDateTime.now())
                .build();

        participantRepository.save(participant);


        return EventParticipantDto.builder()
                .userId(userId)
                .status(RegistrationStatus.PENDING)
                .joinedAt(participant.getJoinedAt())
                .name(getFullName(userProfile))
                .avatarUrl(userProfile.getPhotoUrl())
                .build();
    }

    @Transactional
    public void cancelRegistration(Long eventId, Long userId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        EventParticipantId participantId = new EventParticipantId(userId, eventId);
        EventParticipant participant = participantRepository
                .findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        if (participant.getStatus() == RegistrationStatus.CANCELED) {
            throw new ValidationException("Registration is already canceled");
        }

        participant.setStatus(RegistrationStatus.CANCELED);
        participantRepository.save(participant);

        try {
            UserProfileDto userProfile = profileServiceClient.getUserProfile(userId);
            if (userProfile != null && userProfile.getEmail() != null) {
                statusPublisher.sendStatusChangeNotification(
                        userProfile.getEmail(),
                        getFullName(userProfile),
                        event.getTitle(),
                        "CANCELED"
                );
            }
        } catch (Exception e) {
            log.warn("Cancellation successful but notification failed", e);
        }
    }

    private String getFullName(UserProfileDto profile) {
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}
