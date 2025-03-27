package digit.kafka;

import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.kafka.CustomKafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// NOTE: If tracer is disabled change CustomKafkaTemplate to KafkaTemplate in autowiring

@Service
@Slf4j
public class Producer {

    @Autowired
    private CustomKafkaTemplate<String, Object> kafkaTemplate;

    /**
     * pushes message into mentioned topic
     *
     * @param topic Contains the topic the data needs to be pushed to
     * @param value Contains the value that should be pushed to topic
     */
    public void push(String topic, Object value) {

        // Use the kafkaTemplate to send the provided value to the specified topic.
        kafkaTemplate.send(topic, value);
    }
}
