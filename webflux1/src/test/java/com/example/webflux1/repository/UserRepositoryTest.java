package com.example.webflux1.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private final UserRepository userRepository = new UserRepositoryImpl();
    @Test
    void save() {
        User user = User.builder()
                .name("greg")
                .email("greg@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        StepVerifier.create(userRepository.save(user).doOnNext(System.out::println))
                .assertNext(u -> {
                    assertEquals(1L, u.getId());
                    assertEquals("greg", u.getName());
                    assertEquals("greg@fastcampus.com", u.getEmail());
                }).verifyComplete();
    }

    @Test
    void findAll() {
        userRepository.save(User.builder()
                .name("greg")
                .email("greg@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        userRepository.save(User.builder()
                .name("greg2")
                .email("greg2@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        userRepository.save(User.builder()
                .name("greg3")
                .email("greg3@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        StepVerifier.create(userRepository.findAll().doOnNext(System.out::println))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        userRepository.save(User.builder()
                .name("greg")
                .email("greg@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        userRepository.save(User.builder()
                .name("greg2")
                .email("greg2@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        StepVerifier.create(userRepository.findById(2L))
                .assertNext(u -> {
                    assertEquals(2L, u.getId());
                    assertEquals("greg2", u.getName());
                    assertEquals("greg2@fastcampus.com", u.getEmail());
                }).verifyComplete();

    }

    @Test
    void deleteById() {
        userRepository.save(User.builder()
                .name("greg")
                .email("greg@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        userRepository.save(User.builder()
                .name("greg2")
                .email("greg2@fastcampus.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        StepVerifier.create(userRepository.deleteById(2L))
                .assertNext(i -> assertEquals(1, i))
                .verifyComplete();
    }
}