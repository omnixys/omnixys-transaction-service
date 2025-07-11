package com.omnixys.transaction;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

	@Bean
	@ServiceConnection
	KafkaContainer kafkaContainer() {
		return new KafkaContainer(DockerImageName.parse("apache/kafka-native:latest"));
	}

	@Bean
	@ServiceConnection
	MySQLContainer<?> mysqlContainer() {
    	return new MySQLContainer<>(DockerImageName.parse("mysql:8.0")) // Verwende feste Version
        .withUsername("test")
        .withPassword("test")
        .withDatabaseName("testdb")
        .withStartupTimeout(Duration.ofMinutes(2))
        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(2)));
}

}
