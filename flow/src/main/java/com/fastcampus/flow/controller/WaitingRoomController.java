package com.fastcampus.flow.controller;

import com.fastcampus.flow.constant.AppKeys;
import com.fastcampus.flow.service.UserQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {
    private final UserQueueService userQueueService;

    @GetMapping("/waiting-room")
    Mono<Rendering> waitingRoomPage(@RequestParam(name = "queue", defaultValue = "default") String queue,
                                    @RequestParam(name = "user_id") Long userId,
                                    @RequestParam(name = "redirect_url") String redirectUrl,
                                    ServerWebExchange exchange) {
        var key = AppKeys.TOKEN.KEY.formatted(queue);
        var cookieValue = exchange.getRequest().getCookies().getFirst(key);
        var token = cookieValue == null ? "" : cookieValue.getValue();

        // 1. 입장이 허용되어 page redirect 가능?
        // 2, 어디로 이동?
        return userQueueService.isAllowedByToken(queue, userId, token)
                .filter(allowed -> allowed)
                .flatMap(allowed -> Mono.just(Rendering.redirectTo(redirectUrl).build()))
                .switchIfEmpty(userQueueService.registerQueue(queue, userId)
                        .onErrorResume(ex -> userQueueService.getRank(queue, userId))
                        .map(rank ->
                                Rendering.view("waiting-room.html")
                                        .modelAttribute("number", rank)
                                        .modelAttribute("userId", userId)
                                        .modelAttribute("queue", queue)
                                        .build()
                        )
                );
    }
}
