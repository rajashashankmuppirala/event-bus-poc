package com.shashank.eventbuspoc.integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RabbitBusIntegrationTest.TestConfig.class)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RabbitBusIntegrationTest {

    // RabbitMQ Testcontainer
    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management");

    // WireMock servers for external HTTP services
    static WireMockServer notificationServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    static WireMockServer dataflowServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());

    @BeforeAll
    void setUpMocks() {
        notificationServer.start();
        dataflowServer.start();
    }

    @AfterAll
    void tearDownMocks() {
        if (notificationServer.isRunning()) notificationServer.stop();
        if (dataflowServer.isRunning()) dataflowServer.stop();
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        // RabbitMQ connection for Spring Boot / Stream binder
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");

        // Bind two independent consumers to distinct destinations
        registry.add("spring.cloud.stream.bindings.notificationConsumer-in-0.destination", () -> "test.notification.events");
        registry.add("spring.cloud.stream.bindings.dataflowConsumer-in-0.destination", () -> "test.dataflow.events");

        // External HTTP endpoints used by the consumers
        registry.add("app.notification.base-url", () -> "http://localhost:" + notificationServer.port());
        registry.add("app.dataflow.base-url", () -> "http://localhost:" + dataflowServer.port());
    }

    @Autowired
    private StreamBridge streamBridge;

    @Test
    void twoEventTypesAreDispatchedAndProcessedByTheCorrectConsumers() {
        // Arrange WireMock stubs
        notificationServer.stubFor(post(urlPathEqualTo("/notify"))
                .willReturn(aResponse().withStatus(200)));
        dataflowServer.stubFor(post(urlPathEqualTo("/dataflow"))
                .willReturn(aResponse().withStatus(200)));

        // Act: publish two different event types to their destinations
        String notificationPayload = "{\"type\":\"NOTIFICATION\",\"message\":\"hello\"}";
        String dataflowPayload = "{\"type\":\"DATAFLOW\",\"flowName\":\"demo\",\"operation\":\"start\",\"sourceSystem\":\"A\",\"targetSystem\":\"B\"}";

        boolean sentNotification = streamBridge.send("test.notification.events", notificationPayload);
        boolean sentDataflow = streamBridge.send("test.dataflow.events", dataflowPayload);
        if (!sentNotification || !sentDataflow) {
            throw new AssertionError("Failed to send message(s) to destination(s)");
        }

        // Assert: each mock endpoint was called exactly once (await async processing)
        Awaitility.await().atMost(Duration.ofSeconds(20)).untilAsserted(() -> {
            notificationServer.verify(1, postRequestedFor(urlPathEqualTo("/notify")));
            dataflowServer.verify(1, postRequestedFor(urlPathEqualTo("/dataflow")));
        });
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        Consumer<String> notificationConsumer(WebClient.Builder webClientBuilder,
                                              org.springframework.core.env.Environment env) {
            String notificationBase = env.getProperty("app.notification.base-url");
            WebClient client = webClientBuilder.build();

            return message -> {
                client.post().uri(notificationBase + "/notify")
                        .retrieve()
                        .toBodilessEntity()
                        .block(Duration.ofSeconds(5));
            };
        }

        @Bean
        Consumer<String> dataflowConsumer(WebClient.Builder webClientBuilder,
                                          org.springframework.core.env.Environment env) {
            String dataflowBase = env.getProperty("app.dataflow.base-url");
            WebClient client = webClientBuilder.build();

            return message -> {
                client.post().uri(dataflowBase + "/dataflow")
                        .retrieve()
                        .toBodilessEntity()
                        .block(Duration.ofSeconds(5));
            };
        }
    }
}
