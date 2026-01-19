package eventure.event_service.dto;

import eventure.event_service.model.Gender;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventUpdateDto {
    private Long organizerId;
    private String title;
    private String description;
    private Long categoryId;
    private LocalDateTime eventDate;
    private Integer maxParticipants;
    private Short minAge;
    private Short maxAge;
    private Gender requiredGender;
    private String chatLink;
    private Boolean isAlive;
    private Long viewCount;
}
