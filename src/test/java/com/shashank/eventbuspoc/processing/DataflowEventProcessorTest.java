package com.shashank.eventbuspoc.processing;


import com.shashank.eventbuspoc.domain.event.DataflowEvent;
import com.shashank.eventbuspoc.domain.payload.DataflowPayload;
import com.shashank.eventbuspoc.processing.DataflowEventProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataflowEventProcessorTest {

  @Test
  void completesOnSuccess() {
    WebClient client = mock(WebClient.class, RETURNS_DEEP_STUBS);
    when(client.post().uri(any(String.class)).bodyValue(any()).retrieve().toBodilessEntity())
        .thenReturn(Mono.empty());

    var proc = new DataflowEventProcessor(client, "http://localhost/dataflow");
    var event = new DataflowEvent(this, "svc",
        new DataflowPayload("flow","start","src","tgt"));

    StepVerifier.create(proc.process(event)).verifyComplete();
  }
}
