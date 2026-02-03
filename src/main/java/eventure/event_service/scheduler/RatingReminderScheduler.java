package eventure.event_service.scheduler;

import eventure.event_service.client.ProfileServiceClient;
import eventure.event_service.dto.RatingReminderDto;
import eventure.event_service.dto.UserProfileDto;
import eventure.event_service.messaging.RatingReminderPublisher;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.repository.EventParticipantRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingReminderScheduler {

    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;
    private final RatingRepository ratingRepository;
    private final ProfileServiceClient profileServiceClient;
    private final RatingReminderPublisher ratingReminderPublisher;

    @Scheduled(cron = "0 0 * * * *")
    public void sendRatingReminders() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusHours(25);
        LocalDateTime endTime = now.minusHours(24);
        
        List<Event> endedEvents = eventRepository.findEventsEndedInTimeRange(startTime, endTime);

        for (Event event : endedEvents) {
            processEventForReminders(event);
        }
    }

    private void processEventForReminders(Event event) {
        List<EventParticipant> approvedParticipants = 
            participantRepository.findByEventIdAndStatus(event.getId(), RegistrationStatus.APPROVED);
        

        for (EventParticipant participant : approvedParticipants) {
            Long userId = participant.getId().getUserId();
            
            boolean hasRated = ratingRepository.existsByEventIdAndRaterProfileId(event.getId(), userId);
            
            if (!hasRated) {
                sendReminderToUser(userId, event);
            }
        }
    }

    private void sendReminderToUser(Long userId, Event event) {
        try {
            UserProfileDto userProfile = profileServiceClient.getUserProfile(userId);
            
            if (userProfile != null && userProfile.getEmail() != null) {
                RatingReminderDto reminder = RatingReminderDto.builder()
                    .email(userProfile.getEmail())
                    .userName(getFullName(userProfile))
                    .eventTitle(event.getTitle())
                    .eventId(event.getId())
                    .build();
                
                ratingReminderPublisher.sendRatingReminder(reminder);
            } else {
                log.warn("User profile or email not found for user {}", userId);
            }
            
        } catch (Exception e) {
            log.error("Failed to send reminder to user {} for event {}", userId, event.getId(), e);
        }
    }

    private String getFullName(UserProfileDto profile) {
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}
