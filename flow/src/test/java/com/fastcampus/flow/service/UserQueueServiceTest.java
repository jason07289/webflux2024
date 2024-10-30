package com.fastcampus.flow.service;

import com.fastcampus.flow.EmbeddedRedis;
import com.fastcampus.flow.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class UserQueueServiceTest {

    @Autowired
    private UserQueueService userQueueService;

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    @Test
    void registerQueue() {
        StepVerifier.create(userQueueService.registerQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerQueue("default", 101L))
                .expectNext(2L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerQueue("default", 102L))
                .expectNext(3L)
                .verifyComplete();
    }
    @Test
    void registerConflict(){
        StepVerifier.create(userQueueService.registerQueue("default", 100L))
                .expectNext(1L)
                .verifyComplete();
        StepVerifier.create(userQueueService.registerQueue("default", 100L))
                .expectError(ApplicationException.class)
                .verify();
    }

    @Test
    void allowUser() {
    }

    @Test
    void isAllowed() {
    }
}