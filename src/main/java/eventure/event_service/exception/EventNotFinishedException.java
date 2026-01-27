package eventure.event_service.exception;

public class EventNotFinishedException extends RuntimeException {
    public EventNotFinishedException(String message) {
        super(message);
    }
}
