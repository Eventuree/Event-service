package eventure.event_service.exception;

public class ParticipantNotAuthorizedException extends RuntimeException {
    public ParticipantNotAuthorizedException(String message) {
        super(message);
    }
}
