package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.NotificationSubtype;
import com.shashank.eventbuspoc.domain.event.AppEvent;
import com.shashank.eventbuspoc.domain.event.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class NotificationEventProcessor implements AppEventProcessor<NotificationEvent> {
  private static final Logger log = LoggerFactory.getLogger(NotificationEventProcessor.class);
  private final WebClient client;
  private final String endpoint;

  public NotificationEventProcessor(WebClient client, @Qualifier("notificationEndpoint") String notificationEndpoint) {
    this.client = client;
    this.endpoint = notificationEndpoint;
  }

  @Override public boolean supports(AppEvent event) { return event instanceof NotificationEvent; }

  @Override
  public Mono<Void> process(NotificationEvent event) {
    log.info("Processing NotificationEvent {} subtype={}", event.getEventId(), event.getSubtype());
    return client.post()
        .uri(endpoint)
        .bodyValue(new Outbound(
            event.getEventId(),
            event.getSubtype(),
            event.getPayload().subject(),
            event.getPayload().message(),
            event.getPayload().to(),
            event.getPayload().channel()))
        .retrieve()
        .toBodilessEntity()
        .then();
  }

  record Outbound(String id, NotificationSubtype subtype, String subject, String message, String to, String channel) {}
}
