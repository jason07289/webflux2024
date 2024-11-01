package com.fastcampus.flow.controller;

import com.fastcampus.flow.constant.AppKeys;
import com.fastcampus.flow.dto.AllowResourceResponse;
import com.fastcampus.flow.dto.AllowedResourceResponse;
import com.fastcampus.flow.dto.RankNumberResponse;
import com.fastcampus.flow.dto.RegisterResourceResponse;
import com.fastcampus.flow.service.TransferQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class TransferQueueController {
    private final TransferQueueService transferQueueService;
    @PostMapping
    public Mono<RegisterResourceResponse> registerResource(@RequestParam(name="queue", defaultValue = "default") String queue,
                                                           @RequestParam(name= "resource_id") Long resourceId) {
        return transferQueueService.registerQueue(queue, resourceId)
                .map(RegisterResourceResponse::new);
    }

    @PostMapping("/allow")
    public Mono<AllowResourceResponse> allowResource(@RequestParam(name="queue", defaultValue = "default") String queue,
                                                 @RequestParam(name="count") Long count) {
        return transferQueueService.allowResource(queue, count)
                .map(allowed -> new AllowResourceResponse(count, allowed));

    }

    @GetMapping("/allowed")
    public Mono<AllowedResourceResponse> isAllowedResource(@RequestParam(name="queue", defaultValue = "default") String queue,
                                                       @RequestParam(name= "resource_id") Long resourceId,
                                                       @RequestParam(name="token") String token) {
        return transferQueueService.isAllowedByToken(queue, resourceId, token)
                .map(AllowedResourceResponse::new);
    }

    @GetMapping("/rank")
    public Mono<RankNumberResponse> getRankResource(@RequestParam(name="queue", defaultValue = "default") String queue,
                                                @RequestParam(name= "resource_id") Long resourceId) {
        return transferQueueService.getRank(queue, resourceId)
                .map(RankNumberResponse::new);
    }

    @GetMapping("/touch")
    public Mono<String> touch(@RequestParam(name="queue", defaultValue = "default") String queue,
                              @RequestParam(name= "resource_id") Long resourceId,
                              ServerWebExchange exchange) {
        return Mono.defer(()-> transferQueueService.generateToken(queue, resourceId))
                .map(token -> {
                    exchange.getResponse().addCookie(
                            ResponseCookie.from(AppKeys.TOKEN.KEY.formatted(queue), token)
                                    .maxAge(Duration.ofSeconds(300))
                                    .path("/")
                                    .build()
                    );
                    return token;
                });

    }
}
