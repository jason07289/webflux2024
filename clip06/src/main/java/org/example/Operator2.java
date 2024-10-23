package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

public class Operator2 {
    public Flux<Integer> createFluxConcatMap() {
        return Flux.range(1, 10)
                //concatMap은 순서를 보장해준다.
                .concatMap(i -> Flux.range(i*10, 10))
                .delayElements(Duration.ofMillis(100))
                .log();
    }

    public Flux<Integer> createMonoFlatMapMany(){
        return Mono.just(10)
                .flatMapMany(i -> Flux.range(1, i))
                .log();
    }

    public Mono<Integer> createDefaultIfEmpty1() {
        return Mono.just(100)
                .filter(i -> i> 100)
                .defaultIfEmpty(30)
                .log();
    }

    public Mono<Integer> createSwitchIfEmpty1() {
        return Mono.just(100)
                .filter(i -> i> 100)
                .switchIfEmpty(Mono.just(30).map(i->i*2))
                .log();
    }

    public Mono<Integer> createErrorSwitchIfEmpty() {
        return Mono.just(100)
                .filter(i -> i> 100)
                .switchIfEmpty(Mono.error(new RuntimeException("no value")))
                .log();
    }

    public Flux<String> createFluxMerge() {
        return Flux.merge(Flux.fromIterable(List.of("1", "2", "3")), Flux.just("4"))
                .log();
    }

    public Flux<String> mergeMono() {
        return Mono.just("1").mergeWith(Mono.just("2")).mergeWith(Mono.just("3")).log();
    }

    public Flux<String> createFluxZip(){
        return Flux.zip(Flux.just("a", "b", "c"), Flux.just("d", "e", "f"))
                .map(i -> i.getT1() + i.getT2())
                .log();
    }
}
