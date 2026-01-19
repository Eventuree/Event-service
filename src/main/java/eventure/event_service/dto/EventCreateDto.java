package eventure.event_service.dto;

import eventure.event_service.model.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EventCreateDto {
    @NotNull private Long organizerId;
    @NotBlank private String title;
    @NotBlank private String description;
    @NotNull private Long categoryId;
    @NotNull private LocalDateTime eventDate;
    private String location;
    private Integer maxParticipants;
    private Short minAge;
    private Short maxAge;
    private Gender requiredGender;
    private String chatLink;
}
