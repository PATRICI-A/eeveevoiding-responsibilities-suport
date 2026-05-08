package edu.eci.patricia.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI wellnessServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PATRICI.A — M10 Bienestar y Soporte")
                        .description("""
                                Microservicio M10 del proyecto PATRICI.A (eeveevoiding-responsibilities).
                                Expone el directorio institucional de recursos de bienestar del campus ECI.
                                
                                **RF23** — Sección informativa de bienestar universitario.
                                Recursos disponibles por categoría: MENTAL_HEALTH, SPORTS, CULTURE, ACADEMIC_SUPPORT.
                                
                                ## Autenticación
                                Todos los endpoints requieren un **JWT Bearer token**.
                                Haz clic en **Authorize 🔒** e ingresa tu token en el formato: `Bearer <token>`
                                
                                El servicio es de solo lectura para los estudiantes.
                                El contenido es administrado directamente en la base de datos.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("eeveevoiding-responsibilities — Escuela Colombiana de Ingeniería Julio Garavito")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local")))
                // ── Esquema de seguridad JWT — habilita el botón Authorize en la UI ──
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT. Ingrésalo sin el prefijo 'Bearer', la UI lo agrega sola.")));
    }
}