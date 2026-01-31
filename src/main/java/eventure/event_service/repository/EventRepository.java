package eventure.event_service.repository;

import eventure.event_service.model.entity.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    List<Event> findTop10ByOrderByViewCountDesc();

    @Modifying
    @Query("UPDATE Event e SET e.viewCount = e.viewCount + 1 WHERE e.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT e FROM Event e WHERE " +
            "(:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "ORDER BY CASE WHEN e.category.id IN :favoriteIds THEN 0 ELSE 1 END, e.createdAt DESC")
    Page<Event> findAllSortedByFavoriteCategories(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("favoriteIds") Set<Long> favoriteIds,
            Pageable pageable
    );

    List<Event> findByOrganizerId(Long organizerId);

    Page<Event> findAllByOrganizerIdAndEventDateBefore(Long organizerId, LocalDateTime date, Pageable pageable);

    @Query("""
        SELECT e FROM Event e 
        JOIN EventParticipant ep ON e.id = ep.event.id 
        WHERE ep.id.userId = :userId 
        AND ep.status = 'APPROVED' 
        AND e.eventDate < :now
    """)
    Page<Event> findAttendedEvents(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT e FROM Event e 
        LEFT JOIN EventParticipant ep ON e.id = ep.event.id 
        WHERE e.eventDate < :now 
        AND (
            e.organizerId = :userId 
            OR (ep.id.userId = :userId AND ep.status = 'APPROVED')
        )
    """)
    Page<Event> findAllArchivedEvents(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
