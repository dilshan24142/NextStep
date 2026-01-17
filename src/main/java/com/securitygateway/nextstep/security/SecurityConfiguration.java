package com.securitygateway.nextstep.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ✅ Allow Spring Boot static resources (css, js, images etc.)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // ✅ Allow your frontend html pages (served from /static)
                        .requestMatchers(
                                mvc.pattern("/"),
                                mvc.pattern("/index.html"),
                                mvc.pattern("/login.html"),
                                mvc.pattern("/register.html"),
                                mvc.pattern("/verify-otp.html"),   // ✅ ADDED (FIX 401)
                                mvc.pattern("/dashboard.html"),
                                mvc.pattern("/core.html"),
                                mvc.pattern("/club-events.html"),
                                mvc.pattern("/stalls.html"),
                                mvc.pattern("/lost-found.html"),
                                mvc.pattern("/model-papers.html"),
                                mvc.pattern("/study-room.html"),
                                mvc.pattern("/shuttle.html")
                        ).permitAll()

                        // ✅ AUTH API
                        .requestMatchers(mvc.pattern("/api/v1/auth/**")).permitAll()

                        // ✅ SWAGGER
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // 🔒 OTHERS
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider);

        return http.build();
    }
}
