package eventure.event_service.service.serviceImpl;

import eventure.event_service.exception.DuplicateRatingException;
import eventure.event_service.exception.EventNotFinishedException;
import eventure.event_service.exception.ParticipantNotAuthorizedException;
import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.Event;
import eventure.event_service.model.entity.Rating;
import eventure.event_service.repository.EventParticipantRepository;
import eventure.event_service.repository.EventRepository;
import eventure.event_service.repository.RatingRepository;
import eventure.event_service.service.RatingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository participantRepository;

    public Double getOrganizerRating(Long organizerId) {
        return ratingRepository.getAverageRatingForOrganizer(organizerId);
    }

    @Transactional
    public void rateEvent(Long eventId, Integer score, Long raterId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new EventNotFinishedException("Event is not finished yet. You cannot rate it.");
        }

        boolean isApprovedParticipant = participantRepository.existsById_EventIdAndId_UserIdAndStatus(
                eventId,
                raterId,
                RegistrationStatus.APPROVED
        );

        if (!isApprovedParticipant) {
            throw new ParticipantNotAuthorizedException("You were not an approved participant of this event.");
        }

        if (ratingRepository.existsByEventIdAndRaterProfileId(eventId, raterId)) {
            throw new DuplicateRatingException("You have already rated this event.");
        }

        Rating rating = new Rating();
        rating.setEventId(eventId);
        rating.setRaterProfileId(raterId);
        rating.setRatedProfileId(event.getOrganizerId());
        rating.setScore(score);

        ratingRepository.save(rating);
    }
}
