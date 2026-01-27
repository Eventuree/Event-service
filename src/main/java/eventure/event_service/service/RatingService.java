package eventure.event_service.service;

import jakarta.servlet.http.HttpServletRequest;

public interface RatingService {
    Double getOrganizerRating(Long organizerId);

    void rateEvent(Long eventId, Integer score, Long raterId);

    Long extractUserId(HttpServletRequest request);
}
