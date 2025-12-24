package eventure.event_service.Controller;

import eventure.event_service.Repository.EventRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final EventRepository eventRepository;

    public HealthController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public ResponseEntity<String> getHealth(){
        try {
            eventRepository.findById(1L);
        }
        catch (DataAccessException ex){
            String message = "The database is not available: " + ex.getMessage() +
                    ". Service is unhealthy";
            return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
        }

        return ResponseEntity.ok().body("The service is healthy!");
    }
}
