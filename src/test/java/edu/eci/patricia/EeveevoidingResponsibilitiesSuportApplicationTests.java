package edu.eci.patricia;



import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring Boot integration test that verifies the application context loads successfully.
 *
 * <p>Inline {@code @TestPropertySource} properties take precedence over OS environment
 * variables (including CI-injected {@code SPRING_DATASOURCE_URL}), ensuring H2 is always
 * used for this test regardless of the execution environment.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-for-ci-only-at-least-32-chars"
})
class   EeveevoidingResponsibilitiesSuportApplicationTests {

    @Test
    void contextLoads() {
    }
}