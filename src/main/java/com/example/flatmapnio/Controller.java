package com.example.flatmapnio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/")
public class Controller {

  @Autowired
  MyService myService;

  @PostMapping
  public Mono<ResponseEntity<byte[]>> test(@RequestBody byte[] body) {
    return Mono.just(body)
      .flatMap(bytes -> Mono.fromCallable(() -> bytes))
      .doOnNext(d -> myService.requestProcess(d).subscribe())
      .map(ignored -> ResponseEntity.status(HttpStatus.OK).<byte[]>build())
      .onErrorResume(e -> Mono.just(buildErrorResponse(e)));
  }

  @PostMapping("/200")
  public Mono<ResponseEntity<byte[]>> test200(@RequestBody Map<String, Object> map) {
    return Mono.just(ResponseEntity.ok().build());
  }

  private ResponseEntity<byte[]> buildErrorResponse(Throwable e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}
