package eventure.event_service.dto;

import eventure.event_service.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
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
    private String photo;
    private String chatLink;
    private Boolean isAlive;
    private Long viewCount;
}
