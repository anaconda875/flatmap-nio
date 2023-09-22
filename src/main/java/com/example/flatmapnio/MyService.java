package com.example.flatmapnio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
public class MyService {

  @Autowired
  WebClient webClient;

  public Mono<ResponseEntity<byte[]>> requestProcess(byte[] request) {
    return Mono.fromCallable(() -> {
      Map<String, Object> payload = buildPayload(request);
      return sendMessage(payload)
        .toEntity(byte[].class)
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(b -> {
          System.out.println(Thread.currentThread().getName());
        });
    })
      .flatMap(m -> m.subscribeOn(Schedulers.boundedElastic()))
      .flatMap(ignored -> {
        System.out.println(Thread.currentThread().getName());
        return createResponse(null);
      })
      .doOnNext(ignored -> {
        System.out.println(Thread.currentThread().getName());
      })
      .subscribeOn(Schedulers.boundedElastic());
  }

  public WebClient.ResponseSpec sendMessage(Map<String, Object> req) {
    return webClient
      .method(HttpMethod.POST)
      .uri("/200")
      .body(Mono.just(req), Map.class)
      .retrieve();
  }

  public Map<String, Object> buildPayload(byte[] request) {
    return Map.of("abc", "xyz");
  }

  public Mono<ResponseEntity<byte[]>> createResponse(WebClient.ResponseSpec responseSpec) {
    return Mono.just(ResponseEntity.status(HttpStatus.OK).build());
  }
}
