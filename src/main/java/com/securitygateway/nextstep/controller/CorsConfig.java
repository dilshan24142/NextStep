package com.securitygateway.nextstep.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // හැම API එකකටම ඉඩ දෙන්න
                        .allowedOrigins("http://localhost:5175") // Frontend එක දුවන තැන
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // කරන්න පුළුවන් වැඩ
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}