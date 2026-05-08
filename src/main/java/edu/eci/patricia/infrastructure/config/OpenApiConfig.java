package edu.eci.patricia.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
 
                                El servicio es de solo lectura para los estudiantes.
                                El contenido es administrado directamente en PostgreSQL.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("eeveevoiding-responsibilities — Escuela Colombiana de Ingeniería Julio Garavito")))
                .servers(List.of(
                        new Server().url("http://localhost:8083").description("Local")));
    }
}