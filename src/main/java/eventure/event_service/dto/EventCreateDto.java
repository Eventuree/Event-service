package eventure.event_service.dto;

import eventure.event_service.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Setter
@Getter
public class EventCreateDto {
    @NotNull
    private Long organizerId;
    @NotBlank
    private String title;
    @NotBlank private String description;
    @NotNull private Long categoryId;
    @NotNull private LocalDateTime eventDate;
    private Integer maxParticipants;
    private Short minAge;
    private Short maxAge;
    private Gender requiredGender;
    private MultipartFile photo;
    private String chatLink;
}
