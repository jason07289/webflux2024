package com.example.webflux1.service;

import com.example.webflux1.client.PostClient;
import com.example.webflux1.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    //to mvc server
    private final PostClient postClient;
    public Mono<PostResponse> getPostContent(Long id) {
        return postClient.getPost(id)
                .onErrorResume(error ->
                        Mono.just(new PostResponse(id.toString(), "Fallback data %d".formatted(id))));
    }

    public Flux<PostResponse> getMultiplePostContent(List<Long> ids) {
        return Flux.fromIterable(ids)
                //각각의 postClient 요청에 대해 비동기 처리가 가능하다.
                .flatMap(this::getPostContent)
                .log();
    }
    public Flux<PostResponse> getParallelMultiplePostContent(List<Long> ids) {
        return Flux.fromIterable(ids)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(this::getPostContent)
                .log()
                .sequential()
                ;
    }
}
