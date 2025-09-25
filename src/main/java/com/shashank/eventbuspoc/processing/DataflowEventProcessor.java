package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.event.AppEvent;
import com.shashank.eventbuspoc.domain.event.DataflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class DataflowEventProcessor implements AppEventProcessor<DataflowEvent> {
  private static final Logger log = LoggerFactory.getLogger(DataflowEventProcessor.class);
  private final WebClient client;
  private final String endpoint;

  public DataflowEventProcessor(WebClient client, @Qualifier("dataflowEndpoint") String dataflowEndpoint) {
    this.client = client;
    this.endpoint = dataflowEndpoint;
  }

  @Override public boolean supports(AppEvent event) { return event instanceof DataflowEvent; }

  @Override
  public Mono<Void> process(DataflowEvent event) {
    log.info("Processing DataflowEvent {} flow={}", event.getEventId(), event.getPayload().flowName());
    return client.post()
        .uri(endpoint)
        .bodyValue(event.getPayload())
        .retrieve()
        .toBodilessEntity()
        .then();
  }
}
