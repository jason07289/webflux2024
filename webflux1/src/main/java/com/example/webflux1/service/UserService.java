package com.example.webflux1.service;

import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserR2dbcRepository;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserR2dbcRepository userR2dbcRepository;
    private final ReactiveRedisTemplate<String, User> reactiveRedisTemplate;
    public Mono<User> create(String name, String email) {
        return userR2dbcRepository.save(User.builder()
                .name(name)
                .email(email)
                .build()
        );
    }

    public Flux<User> findAll(){
        return userR2dbcRepository.findAll();
    }

    private String getUserCacheKey(Long id) {
        return "users:%d".formatted(id);
    }

    public Mono<User> findById(Long id){
        //redis 조회
        return reactiveRedisTemplate.opsForValue()
                //값이 있으면 바로 get해서 응답
                .get("users:%d".formatted(id))
                //없다면 DB 조회하고 결과를 redis에 저장
                .switchIfEmpty(userR2dbcRepository.findById(id)
                        .flatMap(u -> reactiveRedisTemplate.opsForValue()
                                .set(this.getUserCacheKey(id), u, Duration.ofSeconds(30))
                                .then(Mono.just(u)
                                )
                        )
                );

//        return userR2dbcRepository.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
        return userR2dbcRepository.deleteById(id)
                .then(reactiveRedisTemplate.unlink(getUserCacheKey(id)))
                .then(Mono.empty());
//         Mono.just(1);
    }
    public Mono<Void> deleteByName(String name) {
        return userR2dbcRepository.deleteByName(name);
    }

    public Mono<User> update(Long id, String name, String email) {
        return userR2dbcRepository.findById(id)
                //map 은 반환 값이 대상 Object이고 flatMap은 reactor의 Publisher (Mono / Flux)이다.
                //map은 new Mono를 리턴, sync operation,  map은 단순히 동기적인 mapper 함수를 사용해 element를 다른 타입 및 값으로 변환
//                .map()
                //flatMap은 기존 값이 새로운 mono값으로 변형된다? async operation, flux to flux의 경우에 non-blocking 처리가 가능하므로 flatMap이 유리하다.
                //새로운 inner Publisher를 만드는게 핵심? inner Flux를 새로 구독하는 것이 핵심?
                .flatMap(u -> {
                    u.setName(name);
                    u.setEmail(email);
                    return userR2dbcRepository.save(u);
                })
                //unlink는 비동기식으로 삭제하는 로직
                .flatMap(u -> reactiveRedisTemplate.unlink(getUserCacheKey(id))
                        .then(Mono.just(u))
                );
    }



}
