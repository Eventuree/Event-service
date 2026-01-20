package eventure.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeNotificationDto {
    private String userEmail;
    private String userName;
    private String eventTitle;
    private String newStatus;
}