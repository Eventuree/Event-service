package eventure.event_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NotificationSendingException extends RuntimeException {

  public NotificationSendingException(String message) {
    super(message);
  }

  public NotificationSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}