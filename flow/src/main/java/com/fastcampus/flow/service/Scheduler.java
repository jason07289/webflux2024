package com.fastcampus.flow.service;

import com.fastcampus.flow.constant.AppKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuples;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final TransferQueueService transferQueueService;

    @Value("${scheduler.enabled}")
    private Boolean scheduling = false;



    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void scheduledAllowResource() {
        if(!scheduling) {
            return;
        }
        log.info("scheduling...");
        var maxAllowResourceCount = 3L;
        reactiveRedisTemplate.scan(ScanOptions.scanOptions().match(AppKeys.WAITING_FOR_SCAN.KEY).count(100).build())
                .map(key->key.split(":")[2])
                .flatMap(queue -> transferQueueService.allowResource(queue, maxAllowResourceCount).map(allowed -> Tuples.of(queue, allowed)))
                .doOnNext(t -> log.info("Tried %d and allowed %d resources of %s queue".formatted(maxAllowResourceCount, t.getT2(), t.getT1())))
                .subscribe();
    }
}
