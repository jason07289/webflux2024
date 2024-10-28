package com.example.webflux1.config;

import com.example.webflux1.repository.User;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        reactiveRedisTemplate.opsForValue().get("1")
                .doOnSuccess(i -> log.info("initialize redis connection"))
                .doOnError(e -> {
                    log.error("failed to initialize redis connection: {}", e.getMessage());
                    SpringApplication.exit(event.getApplicationContext(), () -> -110);
                })
                .subscribe();
    }

    @Bean
    public ReactiveRedisTemplate<String, User> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        var objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
        Jackson2JsonRedisSerializer<User> jsonSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, User.class);

        RedisSerializationContext<String, User> serializationContext = RedisSerializationContext
                .<String, User>newSerializationContext()
                .key(RedisSerializer.string())
                .value(jsonSerializer)
                .hashKey(RedisSerializer.string())
                .hashValue(jsonSerializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);

    }
}
