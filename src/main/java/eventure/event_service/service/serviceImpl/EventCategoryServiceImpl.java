package eventure.event_service.service.serviceImpl;

import eventure.event_service.model.entity.EventCategory;
import eventure.event_service.repository.EventCategoryRepository;
import eventure.event_service.service.EventCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCategoryServiceImpl implements EventCategoryService {

    private final EventCategoryRepository categoryRepository;

    @Override
    public List<EventCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public EventCategory createCategory(EventCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public EventCategory updateCategory(Long id, EventCategory categoryDetails) {
        EventCategory category =
                categoryRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
