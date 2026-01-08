package eventure.event_service.Repository;

import eventure.event_service.Model.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

}
