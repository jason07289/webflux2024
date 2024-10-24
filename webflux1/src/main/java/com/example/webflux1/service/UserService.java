package com.example.webflux1.service;

import com.example.webflux1.repository.User;
import com.example.webflux1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Mono<User> create(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .build()
        );
    }

    public Flux<User> findAll(){
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id){
        return userRepository.findById(id);
    }

    public Mono<Integer> deleteById(Long id) {
        return userRepository.deleteById(id);
    }

    public Mono<User> update(Long id, String name, String email) {
        return userRepository.findById(id)
                //map 은 반환 값이 대상 Object이고 flatMap은 reactor의 Publisher (Mono / Flux)이다.
                //map은 new Mono를 리턴, sync operation,  map은 단순히 동기적인 mapper 함수를 사용해 element를 다른 타입 및 값으로 변환
//                .map()
                //flatMap은 기존 값이 새로운 mono값으로 변형된다? async operation, flux to flux의 경우에 non-blocking 처리가 가능하므로 flatMap이 유리하다.
                //새로운 inner Publisher를 만드는게 핵심? inner Flux를 새로 구독하는 것이 핵심?
                .flatMap(u -> {
                    u.setName(name);
                    u.setEmail(email);
                    return userRepository.save(u);
                });
    }

}
