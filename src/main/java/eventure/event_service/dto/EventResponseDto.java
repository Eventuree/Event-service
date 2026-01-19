package eventure.event_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import eventure.event_service.model.EventStatus;
import eventure.event_service.model.Gender;
import eventure.event_service.model.entity.EventCategory;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventResponseDto {
    private Long id;
    private Long organizerId;
    private String title;
    private String description;
    private EventStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer maxParticipants;
    private EventCategory category;
    private String bannerPhotoUrl;
    private String location;
    private Short minAge;
    private Short maxAge;
    private Gender requiredGender;
    private String chatLink;
    private Boolean isAlive;
    private Long viewCount;
}
