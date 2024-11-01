package com.fastcampus.flow.service;

import com.fastcampus.flow.EmbeddedRedis;
import com.fastcampus.flow.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class TransferQueueServiceTest {

    @Autowired
    private TransferQueueService transferQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection redisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        redisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    void registerQueue() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(transferQueueService.registerQueue("default", 101L))
                .expectNext(2L)
                .verifyComplete();
        StepVerifier.create(transferQueueService.registerQueue("default", 102L))
                .expectNext(3L)
                .verifyComplete();
    }
    @Test
    void registerConflict(){
        StepVerifier.create(transferQueueService.registerQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(transferQueueService.registerQueue("default", 100L))
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    void emptyAllowResource() {
        StepVerifier.create(transferQueueService.allowResource("default", 3L))
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void allowResource() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                .then(transferQueueService.registerQueue("default", 101L))
                .then(transferQueueService.registerQueue("default", 102L))
                .then(transferQueueService.allowResource("default", 2L))
                )
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void allowResource2() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.registerQueue("default", 101L))
                        .then(transferQueueService.registerQueue("default", 102L))
                        .then(transferQueueService.allowResource("default", 5L))
                )
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void allowResourceAfterRegisterQueue() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.registerQueue("default", 101L))
                        .then(transferQueueService.registerQueue("default", 102L))
                        .then(transferQueueService.allowResource("default", 3L))
                        .then(transferQueueService.registerQueue("default", 200L))
                )
                .expectNext(1L)
                .verifyComplete();
    }
    @Test
    void isNotAllowed() {
        StepVerifier.create(transferQueueService.isAllowed("default", 100L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isNotAllowed2() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.allowResource("default", 3L))
                        .then(transferQueueService.isAllowed("default", 101L))
                )
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void isAllowed() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.allowResource("default", 3L))
                        .then(transferQueueService.isAllowed("default", 100L))
                )
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void getRank() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.getRank("default", 100L))
                )
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(transferQueueService.registerQueue("default", 101L)
                        .then(transferQueueService.getRank("default", 101L))
                )
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void getEmptyRank() {
        StepVerifier.create(transferQueueService.registerQueue("default", 100L)
                        .then(transferQueueService.getRank("default", 100L))
                )
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(transferQueueService.getRank("default", 101L)
                )
                .expectNext(-1L)
                .verifyComplete();
    }


    @Test
    void isAllowedByToken() {
        StepVerifier.create(transferQueueService.isAllowedByToken("default", 100L, "d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void generateToken() {
        StepVerifier.create(transferQueueService.generateToken("default", 100L))
                .expectNext("d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8")
                .verifyComplete();
    }
}