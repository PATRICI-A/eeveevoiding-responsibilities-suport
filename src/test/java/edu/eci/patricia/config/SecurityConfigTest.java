package edu.eci.patricia.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {SecurityConfig.class, JwtAuthFilter.class})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-for-unit-test-only-at-least-32-chars"
})
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("SecurityConfig loads successfully")
    void contextLoads() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    @DisplayName("SecurityConfig has EnableWebSecurity annotation")
    void hasEnableWebSecurity() {
        assertThat(SecurityConfig.class.getAnnotation(EnableWebSecurity.class)).isNotNull();
    }
}
