package eventure.event_service.repository;

import eventure.event_service.model.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("SELECT COALESCE(AVG(r.score), 0.0) FROM Rating r WHERE r.ratedProfileId = :organizerId")
    Double getAverageRatingForOrganizer(@Param("organizerId") Long organizerId);

    boolean existsByEventIdAndRaterProfileId(Long eventId, Long raterId);
}