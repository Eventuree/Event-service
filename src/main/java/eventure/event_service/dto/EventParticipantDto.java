package eventure.event_service.dto;

import eventure.event_service.model.RegistrationStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventParticipantDto {
    private Long userId;
    private String name;
    private String avatarUrl;
    private RegistrationStatus status;
    private LocalDateTime joinedAt;
}
