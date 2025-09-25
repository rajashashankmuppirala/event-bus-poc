package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.event.AppEvent;
import reactor.core.publisher.Mono;

public interface AppEventProcessor<T extends AppEvent> {
  boolean supports(AppEvent event);
  Mono<Void> process(T event);
}
