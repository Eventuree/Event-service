package eventure.event_service.service;

import eventure.event_service.dto.EventParticipantDto;
import eventure.event_service.model.RegistrationStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventParticipantService {
    List<EventParticipantDto> getParticipants(Long eventId, Long currentUserId);

    void changeStatus(
            Long eventId, Long participantId, RegistrationStatus newStatus, Long currentUserId);

    Long extractUserId(HttpServletRequest request);

    EventParticipantDto registerParticipant(Long eventId, Long userId);

    void cancelRegistration(Long eventId, Long userId);
}
