package com.shashank.eventbuspoc.bus;


import com.shashank.eventbuspoc.domain.event.AppEvent;
import com.shashank.eventbuspoc.processing.EventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppEventListener {
  private static final Logger log = LoggerFactory.getLogger(AppEventListener.class);
  private final EventDispatcher dispatcher;

  public AppEventListener(EventDispatcher dispatcher) { this.dispatcher = dispatcher; }

  @EventListener
  public void on(AppEvent event) {
    log.info("Received AppEvent {} type={}", event.getEventId(), event.getEventType());
    dispatcher.dispatch(event)
        .doOnError(err -> log.error("Processing failed for {}: {}", event.getEventId(), err.getMessage(), err))
        .subscribe();
  }
}
