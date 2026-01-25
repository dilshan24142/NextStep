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
                registry.addMapping("/**") // Allow all APIs
                        .allowedOrigins("http://localhost:5173") // Where the frontend runs
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Actions that can be performed
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}