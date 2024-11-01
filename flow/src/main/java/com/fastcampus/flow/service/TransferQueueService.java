package com.fastcampus.flow.service;

import com.fastcampus.flow.constant.AppKeys;
import com.fastcampus.flow.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransferQueueService {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;


    public Mono<Long> registerQueue(final String queue, final Long resourceId) {
        var unixTimestamp = Instant.now().getEpochSecond();
        return reactiveRedisTemplate.opsForZSet().add(AppKeys.WAITING.KEY.formatted(queue), resourceId.toString(), unixTimestamp)
                .filter(i->i)
                .switchIfEmpty(Mono.error(ErrorCode.QUEUE_ALREADY_REGISTERED.build()))
                .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(AppKeys.WAITING.KEY.formatted(queue), resourceId.toString()))
                .map(i-> i>=0 ? i+1 : i);
    }

    public Mono<Long> allowResource(final String queue, final Long count) {
        return reactiveRedisTemplate.opsForZSet().popMin(AppKeys.WAITING.KEY.formatted(queue), count)
                .flatMap(member -> reactiveRedisTemplate.opsForZSet()
                        .add(AppKeys.PROCEED.KEY.formatted(queue), Objects.requireNonNull(member.getValue()), Instant.now().getEpochSecond())
                )
                .count();
    }
    public Mono<Boolean> isAllowed(final String queue, final Long resourceId) {
        return reactiveRedisTemplate.opsForZSet().rank(AppKeys.PROCEED.KEY.formatted(queue), resourceId.toString())
                .defaultIfEmpty(-1L)
                .map(rank -> rank >= 0);
    }

    public Mono<Boolean> isAllowedByToken(final String queue, final Long resourceId, final String token) {
        return this.generateToken(queue, resourceId)
                .filter(gen -> gen.equalsIgnoreCase(token))
                .map(i -> true)
                .defaultIfEmpty(false);
    }

    public Mono<Long> getRank(final String queue, final Long resourceId) {
        return reactiveRedisTemplate.opsForZSet().rank(AppKeys.WAITING.KEY.formatted(queue), resourceId.toString())
                .defaultIfEmpty(-1L)
                .map(rank -> rank >= 0 ? rank + 1 : rank);
    }

    public Mono<String> generateToken(final String queue, final Long resourceId) {
        //sha256
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            var input = "resource-queue-%s-%d".formatted(queue, resourceId);
            byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte aByte : encodedHash) {
                hexString.append(String.format("%02x", aByte));
            }
            return Mono.just(hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            throw ErrorCode.INTERNAL_SERVER_ERROR.build(e.toString());
        }
    }
}
