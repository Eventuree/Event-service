package eventure.event_service.messaging;

import eventure.event_service.dto.RatingReminderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingReminderPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user-events}")
    private String exchange;

    @Value("${rabbitmq.routing-key.rating-events}")
    private String routingKey;

    public void sendRatingReminder(RatingReminderDto reminder) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, reminder);
        } catch (Exception e) {
            log.error("Failed to send rating reminder to RabbitMQ", e);
        }
    }
}
