package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders()
                        .exposedHeaders("Set-Cookie")
                        .allowCredentials(true) // 쿠키를 주고 받을 수 있게 설정
                        .allowedOrigins("http://13.124.8.41", "https://13.124.8.41",
                                "http://localhost:3000", "http://localhost:8080");

            }
        };
    }
}
