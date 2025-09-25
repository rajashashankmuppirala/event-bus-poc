package com.shashank.eventbuspoc.domain.event;


import com.shashank.eventbuspoc.domain.EventType;
import com.shashank.eventbuspoc.domain.payload.DataflowPayload;

public class DataflowEvent extends AppEvent {
  private DataflowPayload payload;

  public DataflowEvent() {}

  public DataflowEvent(Object source, String originService, DataflowPayload payload) {
    super(source, originService, EventType.DATAFLOW);
    this.payload = payload;
  }

  public DataflowPayload getPayload() { return payload; }
}
