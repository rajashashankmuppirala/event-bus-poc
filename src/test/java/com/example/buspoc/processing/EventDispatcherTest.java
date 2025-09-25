package com.example.buspoc.processing;


import com.shashank.eventbuspoc.domain.EventType;
import com.shashank.eventbuspoc.domain.event.AppEvent;
import com.shashank.eventbuspoc.processing.AppEventProcessor;
import com.shashank.eventbuspoc.processing.EventDispatcher;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class EventDispatcherTest {

  static class DummyEvent extends AppEvent {
    public DummyEvent() { super(new Object(), "origin", EventType.NOTIFICATION); }
  }

  static class SupportingProcessor implements AppEventProcessor<DummyEvent> {
    @Override public boolean supports(AppEvent event) { return event instanceof DummyEvent; }
    @Override public Mono<Void> process(DummyEvent event) { return Mono.empty(); }
  }

  static class NonSupportingProcessor implements AppEventProcessor<DummyEvent> {
    @Override public boolean supports(AppEvent event) { return false; }
    @Override public Mono<Void> process(DummyEvent event) { return Mono.empty(); }
  }

  @Test
  void routesToSupportingProcessor() {
    var dispatcher = new EventDispatcher(List.of(new NonSupportingProcessor(), new SupportingProcessor()));
    StepVerifier.create(dispatcher.dispatch(new DummyEvent())).verifyComplete();
  }

  @Test
  void errorsWhenNoProcessor() {
    var dispatcher = new EventDispatcher(List.of(new NonSupportingProcessor()));
    StepVerifier.create(dispatcher.dispatch(new DummyEvent())).expectError(IllegalArgumentException.class).verify();
  }
}
