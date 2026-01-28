package com.securitygateway.nextstep.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/*",
            "/error/*",
            "/"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   MvcRequestMatcher.Builder mvc) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Public
                        .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // 📦 LOST & FOUND MODULE

                        // View all posts / single post
                        .requestMatchers(HttpMethod.GET, "/api/v1/lostfound/**").authenticated()

                        // Create post (Admin + Student)
                        .requestMatchers(HttpMethod.POST, "/api/v1/lostfound").hasAnyRole("ADMIN", "STUDENT")

                        // Update post (Admin OR owner handled in service)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/lostfound/**").hasAnyRole("ADMIN", "STUDENT")

                        // Delete post (Admin OR owner handled in service)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/lostfound/**").hasAnyRole("ADMIN", "STUDENT")

                        // Add comment
                        .requestMatchers(HttpMethod.POST, "/api/v1/lostfound/*/comments").hasAnyRole("ADMIN", "STUDENT")

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
