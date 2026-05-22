package edu.eci.patricia.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the wellness support service.
 * Configures stateless JWT-based authentication and endpoint access rules.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Defines the security filter chain with JWT support and CSRF disabled.
     *
     * <p>Public endpoints:</p>
     * <ul>
     *   <li>{@code /swagger-ui/**} — Swagger UI assets</li>
     *   <li>{@code /v3/api-docs/**} — OpenAPI documentation</li>
     *   <li>{@code /actuator/health} — health check</li>
     *   <li>{@code /actuator/info} — service info</li>
     * </ul>
     *
     * <p>All other endpoints require a valid JWT Bearer token.</p>
     *
     * @param http the {@link HttpSecurity} instance to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides a no-op {@link UserDetailsService} to suppress Spring Boot's
     * auto-configuration of an in-memory user (and the generated-password warning).
     * This service is JWT-only; username/password auth is disabled.
     *
     * @return a {@link UserDetailsService} that rejects all lookups
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "This service uses JWT authentication only");
        };
    }
}
