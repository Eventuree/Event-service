package eventure.event_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ratings")
@Data
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "rater_profile_id", nullable = false)
    private Long raterProfileId;

    @Column(name = "rated_profile_id", nullable = false)
    private Long ratedProfileId;

    private Integer score;
}