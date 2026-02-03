package eventure.event_service.repository;

import eventure.event_service.model.RegistrationStatus;
import eventure.event_service.model.entity.EventParticipant;
import eventure.event_service.model.entity.EventParticipantId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventParticipantRepository
        extends JpaRepository<EventParticipant, EventParticipantId> {
    List<EventParticipant> findById_EventId(Long eventId);

    long countById_EventIdAndStatus(Long eventId, RegistrationStatus status);

    boolean existsById(EventParticipantId id);

    boolean existsById_EventIdAndId_UserIdAndStatus(Long eventId, Long userId, RegistrationStatus status);

    @Query("SELECT ep FROM EventParticipant ep " +
            "JOIN FETCH ep.event e " +
            "JOIN FETCH e.category " +
            "WHERE ep.id.userId = :userId AND ep.status = :status")
    List<EventParticipant> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") RegistrationStatus status);


    @Query("SELECT ep FROM EventParticipant ep " +
            "WHERE ep.id.eventId = :eventId AND ep.status = :status")
    List<EventParticipant> findByEventIdAndStatus(
            @Param("eventId") Long eventId,
            @Param("status") RegistrationStatus status
    );

}
