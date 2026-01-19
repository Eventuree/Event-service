package eventure.event_service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class UserHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Читаємо хедер з Postman
        String userIdHeader = httpRequest.getHeader("X-User-Id");

        // Якщо хедер є - кладемо його в атрибути.
        // Саме звідси його потім дістане твій participantService.extractUserId()
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            // Можна класти як String або Long, твій сервіс вміє обробляти обидва варіанти
            httpRequest.setAttribute("userId", Long.parseLong(userIdHeader));
        }

        chain.doFilter(request, response);
    }
}
