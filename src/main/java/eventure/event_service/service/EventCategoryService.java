package eventure.event_service.service;

import eventure.event_service.model.entity.EventCategory;
import java.util.List;

public interface EventCategoryService {
    List<EventCategory> getAllCategories();

    EventCategory createCategory(EventCategory category);

    EventCategory updateCategory(Long id, EventCategory category);

    void deleteCategory(Long id);
}
