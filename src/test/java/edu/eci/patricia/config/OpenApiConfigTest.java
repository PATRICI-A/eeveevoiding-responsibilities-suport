package edu.eci.patricia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    @DisplayName("customOpenAPI creates API with title, version, security scheme")
    void customOpenAPI_createsValidApi() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI api = config.customOpenAPI();

        Info info = api.getInfo();
        assertThat(info.getTitle()).isEqualTo("PATRICIA Wellness & Support Service API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getContact()).isNotNull();
        assertThat(info.getContact().getEmail()).isEqualTo("carreaporfa@gmail.com");

        assertThat(api.getComponents()).isNotNull();
        SecurityScheme scheme = api.getComponents().getSecuritySchemes().get("bearerAuth");
        assertThat(scheme).isNotNull();
        assertThat(scheme.getType()).isEqualTo(SecurityScheme.Type.HTTP);
        assertThat(scheme.getScheme()).isEqualTo("bearer");
        assertThat(scheme.getBearerFormat()).isEqualTo("JWT");

        assertThat(api.getSecurity()).isNotEmpty();
        SecurityRequirement requirement = api.getSecurity().get(0);
        assertThat(requirement).isNotNull();
    }
}
