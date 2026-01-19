package eventure.event_service.dto;

import eventure.event_service.model.RegistrationStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private RegistrationStatus status;
}
