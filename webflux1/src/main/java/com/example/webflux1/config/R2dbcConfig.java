package com.example.webflux1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcConfig implements ApplicationListener<ApplicationReadyEvent> {
    private final DatabaseClient databaseClient;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //reactor에서 sql 보내고 subscribe 하는 로직
        databaseClient.sql("SELECT 1").fetch().one()
                .subscribe(
                        success -> {
                            log.info("initialize r2dbc database connection.");
                        },
                        error -> {
                            log.info("fail to initialize r2dbc database connection.");
                            //connection test 오류시 서비스 종료
                            SpringApplication.exit(event.getApplicationContext(), () -> -110);
                        }
                );
    }

}
