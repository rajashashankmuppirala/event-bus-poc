package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.NotificationSubtype;
import com.shashank.eventbuspoc.domain.event.NotificationEvent;
import com.shashank.eventbuspoc.domain.payload.NotificationPayload;
import com.shashank.eventbuspoc.processing.NotificationEventProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationEventProcessorTest {

  @Test
  void completesOnSuccess() {
    WebClient client = mock(WebClient.class, RETURNS_DEEP_STUBS);
    when(client.post().uri(any(String.class)).bodyValue(any()).retrieve().toBodilessEntity())
        .thenReturn(Mono.empty());

    var proc = new NotificationEventProcessor(client, "http://localhost/notify");
    var event = new NotificationEvent(this, "svc",
        NotificationSubtype.EMAIL, new NotificationPayload("s","m","to@x",""));

    StepVerifier.create(proc.process(event)).verifyComplete();
  }
}
