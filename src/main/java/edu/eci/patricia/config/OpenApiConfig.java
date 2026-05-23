package edu.eci.patricia.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger documentation configuration for the wellness support service.
 * Registers the Bearer JWT security scheme and global security requirement.
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    /**
     * Builds the customised OpenAPI metadata including JWT security scheme.
     *
     * @return a fully configured {@link OpenAPI} bean
     */
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Provide a valid JWT token obtained from the authentication service");

        return new OpenAPI()
                .info(new Info()
                        .title("PATRICIA Wellness & Support Service API")
                        .version("1.0.0")
                        .description("Microservice providing student wellness resources, surveys, recommendations, and behavior report management for the PATRICIA university campus app.")
                        .contact(new Contact()
                                .name("PATRICIA Dev Team")
                                .email("carreaporfa@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, bearerScheme))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
