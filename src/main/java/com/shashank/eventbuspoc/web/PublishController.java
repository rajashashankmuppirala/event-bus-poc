package com.shashank.eventbuspoc.web;


import com.shashank.eventbuspoc.bus.AppEventPublisher;
import com.shashank.eventbuspoc.domain.NotificationSubtype;
import com.shashank.eventbuspoc.domain.event.DataflowEvent;
import com.shashank.eventbuspoc.domain.event.NotificationEvent;
import com.shashank.eventbuspoc.domain.payload.DataflowPayload;
import com.shashank.eventbuspoc.domain.payload.NotificationPayload;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@Validated
public class PublishController {

  private final AppEventPublisher publisher;
  private final String originService;

  public PublishController(AppEventPublisher publisher, @Value("${spring.application.name}") String originService) {
    this.publisher = publisher;
    this.originService = originService;
  }

  @PostMapping("/notification")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void publishNotification(@RequestBody NotificationRequest req) {
    var event = new NotificationEvent(this, originService, req.subtype(),
        new NotificationPayload(req.subject(), req.message(), req.to(), req.channel()));
    publisher.publish(event);
  }

  @PostMapping("/dataflow")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void publishDataflow(@RequestBody DataflowRequest req) {
    var event = new DataflowEvent(this, originService,
        new DataflowPayload(req.flowName(), req.operation(), req.sourceSystem(), req.targetSystem()));
    publisher.publish(event);
  }

  public record NotificationRequest(
          NotificationSubtype subtype, @NotBlank String subject, @NotBlank String message, String to, String channel) {}
  public record DataflowRequest(
      @NotBlank String flowName, @NotBlank String operation, String sourceSystem, String targetSystem) {}
}
