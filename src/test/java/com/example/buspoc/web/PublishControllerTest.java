package com.example.buspoc.web;


import com.shashank.eventbuspoc.bus.AppEventPublisher;
import com.shashank.eventbuspoc.web.PublishController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = PublishController.class, properties = "spring.application.name=test-app")
class PublishControllerTest {

  @Autowired WebTestClient client;
  @MockBean
  AppEventPublisher publisher;

  @Test
  void notificationReturnsAccepted() {
    var json = """
      {"subtype":"EMAIL","subject":"s","message":"m","to":"a@b.com"}
    """;
    client.post().uri("/events/notification")
        .contentType(MediaType.APPLICATION_JSON).bodyValue(json)
        .exchange().expectStatus().isAccepted();
  }

  @Test
  void dataflowReturnsAccepted() {
    var json = """
      {"flowName":"f","operation":"start","sourceSystem":"s","targetSystem":"t"}
    """;
    client.post().uri("/events/dataflow")
        .contentType(MediaType.APPLICATION_JSON).bodyValue(json)
        .exchange().expectStatus().isAccepted();
  }
}
