package com.shashank.eventbuspoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {
  @Bean
  WebClient webClient(WebClient.Builder builder) {
    return builder.build();
  }

  @Bean(name = "notificationEndpoint")
  String notificationEndpoint(@Value("${demo.endpoints.notification}") String url) {
    return url;
  }

  @Bean(name = "dataflowEndpoint")
  String dataflowEndpoint(@Value("${demo.endpoints.dataflow}") String url) {
    return url;
  }
}
