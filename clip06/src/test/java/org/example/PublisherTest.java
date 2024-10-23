package org.example;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class PublisherTest {
    private Publisher publisher = new Publisher();

    @Test
    void startFlux() {
        StepVerifier.create(publisher.startFlux())
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    void startMono() {
        StepVerifier.create(publisher.startMono())
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    void startMonoEmpty() {
        StepVerifier.create(publisher.startMonoEmpty())
                .verifyComplete();
    }
    @Test
    void startMonoError() {
        StepVerifier.create(publisher.startMonoError())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void startFluxString() {
        StepVerifier.create(publisher.startFluxString())
                .expectNextCount(5)
                .verifyComplete();
    }
}