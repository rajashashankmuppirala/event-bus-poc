package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.event.AppEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class EventDispatcher {
  private final List<AppEventProcessor<? extends AppEvent>> processors;

  public EventDispatcher(List<AppEventProcessor<? extends AppEvent>> processors) {
    this.processors = processors;
  }

  @SuppressWarnings("unchecked")
  public Mono<Void> dispatch(AppEvent event) {
    return processors.stream()
        .filter(p -> p.supports(event))
        .findFirst()
        .map(p -> ((AppEventProcessor<AppEvent>) p).process(event))
        .orElseGet(() -> Mono.error(new IllegalArgumentException("No processor for " + event.getClass().getSimpleName())));
  }
}
