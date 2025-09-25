package com.shashank.eventbuspoc.bus;


import com.shashank.eventbuspoc.domain.event.AppEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AppEventPublisher {
  private final ApplicationEventPublisher publisher;
  public AppEventPublisher(ApplicationEventPublisher publisher) { this.publisher = publisher; }
  public void publish(AppEvent event) { publisher.publishEvent(event); }
}
