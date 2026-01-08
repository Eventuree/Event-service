package eventure.event_service.Model.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eventure.event_service.Model.EventStatus;
import eventure.event_service.Model.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id")
    private Long organizerId;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column (name = "original_event_date")
    private LocalDateTime originalEventDate;

    @Column (name = "max_participants")
    private Integer maxParticipants;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private EventCategory category;

    @Column (name = "banner_photo_url")
    private String bannerPhotoUrl;

    private String location;

    @Column (name = "min_age")
    private Short minAge;

    @Column (name = "max_age")
    private Short maxAge;

    @Enumerated(EnumType.STRING)
    @Column (name = "required_gender")
    private Gender requiredGender;

    @Column (name = "chat_link")
    private String chatLink;

    @Column (name = "is_alive")
    private Boolean isAlive;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @com.fasterxml.jackson.annotation.JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
