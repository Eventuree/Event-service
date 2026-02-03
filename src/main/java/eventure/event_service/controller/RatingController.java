package eventure.event_service.controller;

import eventure.event_service.dto.RateEventDto;
import eventure.event_service.service.RatingService;
import eventure.event_service.utils.SecurityHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;
    private final SecurityHelper securityHelper;

    @PostMapping("/api/v1/events/{eventId}/rate")
    public ResponseEntity<Void> rateEvent(
            @PathVariable Long eventId,
            @RequestBody RateEventDto request,
            HttpServletRequest httpRequest
    ) {
        Long userId = securityHelper.extractUserId(httpRequest);

        ratingService.rateEvent(eventId, request.getScore(), userId);

        return ResponseEntity.ok().build();
    }

     @GetMapping("/api/v1/ratings/organizer/{organizerId}")
    public ResponseEntity<Double> getOrganizerRating(@PathVariable Long organizerId) {
        return ResponseEntity.ok(ratingService.getOrganizerRating(organizerId));
    }
}