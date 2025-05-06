package com.gentlecorp.transaction.messaging;

import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.gentlecorp.transaction.messaging.KafkaTopicProperties.TOPIC_SYSTEM_SHUTDOWN;

/**
 * Kafka-Consumer für eingehende Events zur Kontoerstellung und -löschung.
 *
 * <p>
 * Unterstützt folgende Nachrichtenformate:
 * </p>
 *
 * @author
 * @since 05.05.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final ApplicationContext context;

    @Observed(name = "kafka-consume.system-shutdown")
    @KafkaListener(topics = TOPIC_SYSTEM_SHUTDOWN, groupId = "${app.groupId}")
    public void consumeShutDown() {
        log.debug("Shutting down via ApplicationContext");
        log.debug("Bye 🖐🏾");
        ((ConfigurableApplicationContext) context).close();
    }
}
