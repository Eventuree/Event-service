package eventure.event_service.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {

    public Long extractUserId(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isBlank()) {
            throw new IllegalArgumentException("User ID is missing in headers");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid User ID format");
        }
    }
}
