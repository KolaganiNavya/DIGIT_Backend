package digit.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import digit.Service.NotificationService;
import digit.web.models.BirthRegistrationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;

//listens for messages form a kafka topic and process those messages
@Component
@Slf4j
public class NotificationConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * This method listens for messages from the Kafka topic specified in the application properties.
     * When a new message is received, the method processes it by converting it into a BirthRegistrationRequest object
     * and passing it to the NotificationService to handle further actions (e.g., sending notifications).
     *
     * @param record
     * @param topic
     */
    @KafkaListener(topics = {"${btr.kafka.create.topic}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {

            BirthRegistrationRequest request = mapper.convertValue(record, BirthRegistrationRequest.class);
            notificationService.prepareEventAndSend(request);

        } catch (final Exception e) {

            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e);
        }
    }

}
