package com.shashank.eventbuspoc.domain.event;


import com.shashank.eventbuspoc.domain.EventType;
import com.shashank.eventbuspoc.domain.NotificationSubtype;
import com.shashank.eventbuspoc.domain.payload.NotificationPayload;

public class NotificationEvent extends AppEvent {
  private NotificationSubtype subtype;
  private NotificationPayload payload;

  public NotificationEvent() {}

  public NotificationEvent(Object source, String originService,
                           NotificationSubtype subtype, NotificationPayload payload) {
    super(source, originService, EventType.NOTIFICATION);
    this.subtype = subtype;
    this.payload = payload;
  }

  public NotificationSubtype getSubtype() { return subtype; }
  public NotificationPayload getPayload() { return payload; }
}
