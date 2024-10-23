package org.example;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class Operator1 {
    public Flux<Integer> createFluxMap() {
        return Flux.range(2, 5)
                .map(i -> i * 2)
                .log();
    }
    public Flux<Integer> createFluxFilter() {
        return Flux.range(1, 10)
                .filter(i -> i==1)
                .log();
    }

    public Flux<Integer> createFluxFilterTake() {
        return Flux.range(1, 10)
                .filter(i -> i > 5)
                .take(3)
                .log();
    }

    public Flux<Integer> createFluxFlatMap(){
        return Flux.range(1, 10)
                .flatMap(i -> Flux.range(i*10, 10))
                .delayElements(Duration.ofMillis(100))
                .log();
    }

    public Flux<Integer> createFluxFlatMap2(){
        return Flux.range(1, 9)
                .flatMap(i -> Flux.range(1, 9)
                        .map(j -> {
                            System.out.printf("%d * %d = %d\n", i, j, i*j);
                            return i*j;
                        })
                )
                .log();
    }
}
