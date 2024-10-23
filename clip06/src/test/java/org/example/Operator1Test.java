package org.example;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class Operator1Test {
    private final Operator1 operator1 = new Operator1();

    @Test
    void createFluxMap() {
        StepVerifier.create(operator1.createFluxMap())
                .expectNext(4, 6, 8, 10, 12)
                .verifyComplete();

    }

    @Test
    void createFluxFilter() {
        StepVerifier.create(operator1.createFluxFilter())
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void createFluxFilterTake() {
        StepVerifier.create(operator1.createFluxFilterTake())
                .expectNext(6,7,8)
                .verifyComplete();
    }

    @Test
    void createFluxFlatMap() {
        StepVerifier.create(operator1.createFluxFlatMap())
                .expectNextCount(100)
                .verifyComplete();
    }

    @Test
    void createFluxFlatMap2() {
        StepVerifier.create(operator1.createFluxFlatMap2())
                .expectNextCount(81)
                .verifyComplete();
    }
}