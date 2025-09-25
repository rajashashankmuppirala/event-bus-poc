package com.example.buspoc.bus;


import com.shashank.eventbuspoc.bus.AppEventListener;
import com.shashank.eventbuspoc.domain.EventType;
import com.shashank.eventbuspoc.domain.event.AppEvent;
import com.shashank.eventbuspoc.processing.EventDispatcher;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AppEventListenerTest {

  static class DummyEvent extends AppEvent {
    public DummyEvent() { super(new Object(), "origin", EventType.DATAFLOW); }
  }

  @Test
  void dispatchIsInvoked() {
    var dispatcher = mock(EventDispatcher.class);
    when(dispatcher.dispatch(any())).thenReturn(Mono.empty());

    var listener = new AppEventListener(dispatcher);
    listener.on(new DummyEvent());

    verify(dispatcher, times(1)).dispatch(any());
  }
}
