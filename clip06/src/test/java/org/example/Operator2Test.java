package org.example;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class Operator2Test {
    private final Operator2 operator2 = new Operator2();

    @Test
    void createFluxConcatMap() {
        StepVerifier.create(operator2.createFluxConcatMap())
                .expectNextCount(100)
                .verifyComplete();
    }

    @Test
    void createMonoFlatMapMany() {
        StepVerifier.create(operator2.createMonoFlatMapMany())
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void createDefaultIfEmpty1() {
        StepVerifier.create(operator2.createDefaultIfEmpty1())
                .expectNext(30)
                .verifyComplete();
    }

    @Test
    void createSwitchIfEmpty1() {
        StepVerifier.create(operator2.createSwitchIfEmpty1())
                .expectNext(60)
                .verifyComplete();
    }

    @Test
    void createErrorSwitchIfEmpty() {
        StepVerifier.create(operator2.createErrorSwitchIfEmpty())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void createFluxMerge() {
        StepVerifier.create(operator2.createFluxMerge())
                .expectNext("1", "2", "3", "4")
                .verifyComplete();
    }

    @Test
    void mergeMono() {
        StepVerifier.create(operator2.mergeMono())
                .expectNext("1", "2", "3")
                .verifyComplete();
    }

    @Test
    void createFluxZip() {
        StepVerifier.create(operator2.createFluxZip())
                .expectNext("ad", "be", "cf")
                .verifyComplete();
    }
}