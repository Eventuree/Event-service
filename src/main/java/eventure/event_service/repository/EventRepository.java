package eventure.event_service.repository;

import eventure.event_service.model.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findTop10ByOrderByViewCountDesc();
}
