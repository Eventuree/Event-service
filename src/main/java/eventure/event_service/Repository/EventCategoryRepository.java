package eventure.event_service.Repository;

import eventure.event_service.Model.Entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

}
