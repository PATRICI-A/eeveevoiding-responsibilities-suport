package edu.eci.patricia.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI configuration for the Wellness Support Service.
 * Registers the {@link OpenAPI} bean that populates the Swagger UI with service metadata,
 * security schemes, and organised API tags.
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PATRICI.A Wellness Support Service API")
                        .description("""
                                This microservice is responsible for providing wellness support and student \
                                well-being services for the PATRICI.A platform at Universidad ECI.

                                Key responsibilities include:
                                - **Wellness Resources (RF23)**: Management of campus wellness resources across \
                                  multiple categories: EMOTIONAL_SUPPORT, SPORTS, CULTURE, HEALTH. Students can \
                                  browse resources, filter by category, and receive personalised recommendations \
                                  based on survey responses. EMOTIONAL_SUPPORT resources include appointment \
                                  email generation functionality.

                                - **Wellness Survey (PTR23.1 / PTR23.2)**: Student well-being assessment with 10 \
                                  mandatory questions (P01–P10). Responses are analysed to generate personalised \
                                  recommendations (PTR23.2). Students without survey history receive general \
                                  wellness resources as fallback (RN-23.2.2).

                                - **Behavior Reports (RF24)**: Anonymous reporting system for inappropriate \
                                  behaviour. Reports are assigned unique case numbers (format RPT-YYYYMMDD-XXXX). \
                                  Reporter identity is never exposed to the reported party. Students can track \
                                  their own submissions.

                                All write endpoints require a valid Bearer JWT token issued by the authentication \
                                service. The user ID is extracted from the JWT `sub` claim.
                                """)
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        JWT Bearer token authentication for all wellness support endpoints.
                                        
                                        Required for:
                                        - Browsing wellness resources (Wellness Resources tag)
                                        - Submitting and tracking behavior reports (Behavior Reports tag)
                                        - Completing wellness surveys (Wellness Survey tag)
                                        
                                        The token must be obtained from the authentication service.
                                        Include the token in the Authorization header as:
                                        `Authorization: Bearer <your-jwt-token>`
                                        
                                        The user ID is extracted from the JWT `sub` claim.
                                        """)))
                .addTagsItem(new Tag()
                        .name("Wellness Resources")
                        .description("""
                                Endpoints for browsing and managing campus wellness resources (RF23). Supports:
                                - Listing all resources with optional category filter (EMOTIONAL_SUPPORT, SPORTS, \
                                  CULTURE, HEALTH, RECOMMENDATIONS, ALL)
                                - Retrieving individual resource details by ID
                                - Generating appointment mailto links for EMOTIONAL_SUPPORT resources (HU-23-03)
                                - Full CRUD operations for administrators (create, update, delete)
                                
                                The RECOMMENDATIONS category returns personalised resources based on the student's \
                                wellness survey responses (PTR23.2). Students without survey history receive all \
                                active resources as fallback (RN-23.2.2).
                                """))
                .addTagsItem(new Tag()
                        .name("Wellness Survey")
                        .description("""
                                Endpoints for student well-being assessment (PTR23.1 / PTR23.2). Features:
                                - GET /preguntas — Retrieve the 10 mandatory survey questions (P01–P10) with answer options
                                - POST / — Submit survey responses; validates all 10 questions are answered (RN-23.1.1)
                                - GET /recomendaciones — Get personalised resource recommendations based on most \
                                  recent survey; falls back to general resources if no survey exists (RN-23.2.2)
                                
                                Survey responses are used to calculate recommended wellness categories via a \
                                rules engine that maps answer patterns to resource types.
                                """))
                .addTagsItem(new Tag()
                        .name("Behavior Reports")
                        .description("""
                                Endpoints for submitting and tracking inappropriate behavior reports (RF24). Features:
                                - POST / — Submit an anonymous report; receives a unique case number (RPT-YYYYMMDD-XXXX)
                                - GET /{id} — Retrieve a specific report; only accessible by the original reporter
                                - GET /my-reports — List all reports submitted by the authenticated student
                                
                                Reports can reference specific content via `referenceId` (e.g., message ID, post ID). \
                                Reporter identity is never exposed to the reported party. Reports are stored with \
                                status tracking (PENDING, REVIEWED, RESOLVED).
                                """));
    }
}