package eventure.event_service.messaging;

import eventure.event_service.dto.StatusChangeNotificationDto;
import eventure.event_service.exception.NotificationSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user-events}")
    private String exchange;

    @Value("${rabbitmq.routing-key.status-change}")
    private String routingKey;

    public void sendStatusChangeNotification(String email, String userName, String eventTitle, String newStatus) {
        try {
            StatusChangeNotificationDto dto = StatusChangeNotificationDto.builder()
                    .userEmail(email)
                    .userName(userName)
                    .eventTitle(eventTitle)
                    .newStatus(newStatus)
                    .build();

            rabbitTemplate.convertAndSend(exchange, routingKey, dto);
            log.info("Sent status change notification to queue for user: {}", email);

        } catch (AmqpException e) {
            log.error("Failed to send RabbitMQ message for user: {}", email, e);
            throw new NotificationSendingException("Failed to send notification to RabbitMQ", e);
        }
    }
}
