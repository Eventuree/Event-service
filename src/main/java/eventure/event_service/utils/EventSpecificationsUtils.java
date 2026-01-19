package eventure.event_service.utils;

import eventure.event_service.dto.EventFiltersDto;
import eventure.event_service.model.entity.Event;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class EventSpecificationsUtils {

    public static Specification<Event> buildFilters(EventFiltersDto filters) {
        Specification<Event> spec = Specification.unrestricted();

        if (filters == null) {
            return spec;
        }

        if (filters.getDateFrom() != null &&
                filters.getDateTo() != null &&
                filters.getDateFrom().isAfter(filters.getDateTo())) {
            throw new IllegalArgumentException("dateFrom must be before dateTo");
        }

        if (StringUtils.hasText(filters.getKeyword())) {
            spec = spec.and(hasKeywordInTitleOrDescription(filters.getKeyword()));
        }

        if (filters.getCategoryId() != null) {
            spec = spec.and(hasCategory(filters.getCategoryId()));
        }

        if (filters.getDateFrom() != null) {
            spec = spec.and(dateFrom(filters.getDateFrom()));
        }

        if (filters.getDateTo() != null) {
            spec = spec.and(dateTo(filters.getDateTo()));
        }

        if (StringUtils.hasText(filters.getLocation())) {
            spec = spec.and(hasLocation(filters.getLocation()));
        }

        if (filters.getMinAge() != null) {
            spec = spec.and(minAge(filters.getMinAge()));
        }

        if (filters.getMaxAge() != null) {
            spec = spec.and(maxAge(filters.getMaxAge()));
        }

        return spec;
    }

    public static Specification<Event> hasKeywordInTitleOrDescription(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Event> hasCategory(Integer categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Event> dateFrom(LocalDateTime dateFrom) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("eventDate"), dateFrom);
    }

    public static Specification<Event> dateTo(LocalDateTime dateTo) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("eventDate"), dateTo);
    }

    public static Specification<Event> hasLocation(String location) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("location")),
                        "%" + location.toLowerCase() + "%"
                );
    }

    public static Specification<Event> minAge(Short minAge) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("minAge"), minAge);
    }

    public static Specification<Event> maxAge(Short maxAge) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("maxAge"), maxAge);
    }
}
