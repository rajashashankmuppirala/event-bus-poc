# event-bus-poc
# Event Bus PoC — Application Flow

Overview
- Purpose: Demonstrate event-driven messaging with RabbitMQ using Spring Boot, Spring Cloud Stream, and Spring Cloud Bus.
- Core ideas:
  - Publish domain events to RabbitMQ topics.
  - Consume events with function-based consumers.
  - Trigger downstream side effects via HTTP calls to external services (notification and dataflow).
  - Optionally propagate cross-instance management events using Spring Cloud Bus.

Architecture
- Producers: Components publish JSON payloads to logical destinations (topics) via Spring Cloud Stream.
- Broker: RabbitMQ transports messages.
- Consumers: Function beans process specific event types (each bound to a destination).
- External services: 
  - Notification service (invoked via HTTP POST /notify).
  - Dataflow service (invoked via HTTP POST /dataflow).
- Observability/ops: Spring Boot Actuator; Spring Cloud Bus can broadcast management events (e.g., refresh) across instances if enabled.

Message Flow
1. A producer publishes an event to a destination using StreamBridge:
   - Destination for notification events: test.notification.events
   - Destination for dataflow events: test.dataflow.events
2. Spring Cloud Stream routes the event to the RabbitMQ exchange/queue mapped to the destination.
3. Function-based consumers are bound to those destinations:
   - notificationConsumer listens to test.notification.events and calls the notification endpoint.
   - dataflowConsumer listens to test.dataflow.events and calls the dataflow endpoint.
4. Each consumer performs an HTTP POST to its respective service:
   - POST {app.notification.base-url}/notify
   - POST {app.dataflow.base-url}/dataflow
5. If Spring Cloud Bus is used, RemoteApplicationEvent subclasses can be published to broadcast cross-instance events. The project uses the non-deprecated constructor form for RemoteApplicationEvent.

Sequence Diagram

External Interactions
- Notification service:
  - HTTP POST /notify
  - Base URL configured via app.notification.base-url
- Dataflow service:
  - HTTP POST /dataflow
  - Base URL configured via app.dataflow.base-url

Integration Tests (End-to-End)
- Tools:
  - Testcontainers: Starts an ephemeral RabbitMQ in Docker for the test run.
  - WireMock: Spins up mock HTTP servers for the notification and dataflow services.
  - Awaitility: Awaits asynchronous processing before assertions.
- Test scenario:
  1. Start RabbitMQ container and two WireMock servers with dynamic ports.
  2. Dynamically configure the application under test:
     - spring.rabbitmq.* to point at the container.
     - Bind consumers to test.notification.events and test.dataflow.events.
     - app.notification.base-url and app.dataflow.base-url to point at WireMock servers.
  3. Stub WireMock endpoints to return 200 for POST /notify and POST /dataflow.
  4. Publish two messages using StreamBridge:
     - One to test.notification.events (notification event).
     - One to test.dataflow.events (dataflow event).
  5. Await and verify:
     - Notification mock received exactly one POST /notify.
     - Dataflow mock received exactly one POST /dataflow.
- What this proves:
  - Messages are published to RabbitMQ successfully.
  - Each event type is routed to the correct consumer.
  - Consumers perform expected side effects (HTTP POST calls).

Configuration
- RabbitMQ:
  - spring.rabbitmq.host, spring.rabbitmq.port, spring.rabbitmq.username, spring.rabbitmq.password
- Stream bindings:
  - spring.cloud.stream.bindings.notificationConsumer-in-0.destination
  - spring.cloud.stream.bindings.dataflowConsumer-in-0.destination
- External services:
  - app.notification.base-url
  - app.dataflow.base-url
- Spring Cloud Bus (optional):
  - spring.cloud.bus.id, spring.cloud.bus.destination, etc.
  - Actuator endpoint (if exposed): /actuator/busrefresh

Prerequisites
- Java 23 (or set java.version accordingly).
- Docker (required to run Testcontainers-based integration tests).
- Maven.

How to Run
- Run tests (includes integration tests with RabbitMQ container and WireMock):
  - mvn clean verify
- Run unit tests only:
  - mvn -Dtest='!*IntegrationTest' test
- Run the application locally (ensure RabbitMQ reachable or configure containerized RabbitMQ):
  - mvn spring-boot:run
  - Provide spring.rabbitmq.* properties via application.yml or environment variables.

Notes
- The application demonstrates a clean separation of concerns:
  - Messaging concerns handled by Spring Cloud Stream and RabbitMQ.
  - Business-side effects (notification/dataflow) via HTTP calls.
  - Cross-instance orchestration optionally via Spring Cloud Bus.
- Integration tests exercise the full path from publish to side effect, ensuring correct dispatch and processing for distinct event types.