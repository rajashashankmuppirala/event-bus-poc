package com.shashank.eventbuspoc.domain.event;


import com.shashank.eventbuspoc.domain.EventType;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import java.util.UUID;

public abstract class AppEvent extends RemoteApplicationEvent {
  private String eventId;
  private EventType eventType;

  protected AppEvent() {}

  protected AppEvent(Object source, String originService, EventType type) {
    super(source, originService);
    this.eventId = UUID.randomUUID().toString();
    this.eventType = type;
  }

  public String getEventId() { return eventId; }
  public EventType getEventType() { return eventType; }
}
