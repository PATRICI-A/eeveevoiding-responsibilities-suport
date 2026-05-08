package edu.eci.patricia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Enlace mailto generado para solicitud de cita psicológica")
@Getter
@Builder
public class AppointmentMailtoResponse {

    //Todos estos datos por el momento son de ejemplo
    @Schema(description = "ID del recurso MENTAL_HEALTH", example = "1")//Tiene que ser UUID o no se si manejarlo por ID el recurso
    private String resourceId;

    @Schema(description = "Nombre de la psicóloga", example = "Dra. María Pérez")
    private String psychologistName;

    @Schema(description = "Correo destino de la cita", example = "psicologia@escuelaing.edu.co")
    private String appointmentEmail;

    @Schema(description = "Asunto pre-armado del correo",
            example = "Solicitud de cita - Laura González")
    private String subject;

    @Schema(description = "Cuerpo pre-armado del correo con el nombre del estudiante inyectado")
    private String body;

    @Schema(description = "Enlace mailto listo para abrir en cliente de correo",
            example = "mailto:psicologia@escuelaing.edu.co" +
                    "persona = Juan")
    private String mailtoLink;
}