package com.example.flatmapnio;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Config {

  @Bean
  WebClient vasWebClient() {
    return WebClient.builder()
      .baseUrl("http://localhost:8080")
      .build();
  }

}
