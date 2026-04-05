package com.securitygateway.nextstep.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ✅ React (Vite)
                .allowedOrigins("http://localhost:5173")

                // ✅ REST methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // ✅ VERY IMPORTANT for JWT
                .allowedHeaders("*")

                // ❌ JWT does NOT need cookies
                .allowCredentials(false)

                .maxAge(3600);
    }
}
