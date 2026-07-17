package com.healthwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthWatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthWatchApplication.class, args);
    }

}
