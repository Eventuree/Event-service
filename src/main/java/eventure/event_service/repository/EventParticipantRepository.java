package eventure.event_service.repository;

import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.model.entity.EventParticipantId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventParticipantRepository
        extends JpaRepository<EventParticipant, EventParticipantId> {
    List<EventParticipant> findById_EventId(Long eventId);

    long countById_EventIdAndStatus(Long eventId, RegistrationStatus status);

    boolean existsById(EventParticipantId id);
}
