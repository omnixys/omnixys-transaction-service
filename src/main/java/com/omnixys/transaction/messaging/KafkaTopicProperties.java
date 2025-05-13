package com.omnixys.transaction.messaging;

import lombok.RequiredArgsConstructor;

/**
 * Zentrale Konfiguration der Kafka-Topic-Namen.
 * <p>
 * Die Namen folgen dem Schema: {@code <service>.<entities>.<events>}.
 * </p>
 *
 * @author Caleb
 * @since 20.04.2025
 */
@RequiredArgsConstructor
public final class KafkaTopicProperties {

    public static final String TOPIC_LOG_STREAM_LOG_TRANSACTION = "log-Stream.log.transaction";

    public static final String TOPIC_TRANSACTION_CREATE_PAYMENT = "transaction.create.payment";

    public static final String TOPIC_TRANSACTION_SHUTDOWN_ORCHESTRATOR = "transaction.shutdown.orchestrator";
    public static final String TOPIC_TRANSACTION_START_ORCHESTRATOR = "transaction.start.orchestrator";
    public static final String TOPIC_TRANSACTION_RESTART_ORCHESTRATOR = "transaction.restart.orchestrator";

    public static final String TOPIC_ALL_SHUTDOWN_ORCHESTRATOR = "all.shutdown.orchestrator";
    public static final String TOPIC_ALL_START_ORCHESTRATOR = "all.start.orchestrator";
    public static final String TOPIC_ALL_RESTART_ORCHESTRATOR = "all.restart.orchestrator";
}

