package eventure.event_service.Service;

import eventure.event_service.Model.Entity.EventCategory;
import java.util.List;

public interface EventCategoryService {
    List<EventCategory> getAllCategories();

    EventCategory createCategory(EventCategory category);

    EventCategory updateCategory(Long id, EventCategory category);

    void deleteCategory(Long id);
}
