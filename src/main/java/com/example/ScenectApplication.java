package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ScenectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenectApplication.class, args);
    }

}
