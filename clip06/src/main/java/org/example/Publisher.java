package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class Publisher {
//    void startFlux() {
//        Flux.just(1,2,3,4,5);
//        Flux.range(1,5);
//        Flux.fromIterable(List.of(1,2,3));
//
//
//    }
    public Flux<Integer> startFlux(){
        return Flux.range(1, 10).log();
    }
    public Flux<String> startFluxString(){
        return Flux.fromIterable(List.of("1", "a", "q", "65", "b")).log();
    }

    public Mono<Integer> startMono(){
        return Mono.just(1).log();
    }

    public Mono<?> startMonoEmpty(){
        return Mono.empty().log();
    }

    public Mono<?> startMonoError() {
        return Mono.error(new RuntimeException("publish ERROR!"));
    }
}
