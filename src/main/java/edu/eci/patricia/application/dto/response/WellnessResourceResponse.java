package edu.eci.patricia.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Recurso de bienestar")
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WellnessResourceResponse {

    @Schema(description = "ID único del servicio", example = "1")
    private String id;

    @Schema(description = "Nombre del servicio", example = "Servicio de Psicología")
    private String name;

    @Schema(description = "Descripción del servicio", example = "Apoyo psicológico individual para estudiantes")
    private String description;

    @Schema(description = "Teléfono de contacto", example = "EJ: Todavia no se si poner esto")//no se si tenemos un telefono de psicologia
    private String contactPhone;

    @Schema(description = "Correo de contacto general", example = "bienestar@escuelaing.edu.co")//este es un ejemplo pero toca implementar el correo real
    private String contactEmail;

    @Schema(description = "Horarios de atención", example = "Lunes a viernes, 8:00 a.m. – 5:00 p.m.")//Tambien falta ver los horarios reales
    private String schedule;

    @Schema(description = "Categoría del recurso", example = "MENTAL_HEALTH")
    private WellnessCategory category;

    //Para Mental-Healt

    @Schema(description = "Correo de la psicóloga para agendar cita (solo MENTAL_HEALTH)",
            example = "psicologia@escuelaing.edu.co") //Nos faltan los datos reales
    private String appointmentEmail;

    @Schema(description = "Nombre de la psicóloga (solo MENTAL_HEALTH)",
            example = "Dra. María Pérez")
    private String psychologistName;
}