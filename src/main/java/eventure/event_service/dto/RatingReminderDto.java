package eventure.event_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingReminderDto {
    private String email;
    private String userName;
    private String eventTitle;
    private Long eventId;
}
