package edu.eci.patricia.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Enlace mailto generado para solicitud de cita psicológica")
@Getter
@Builder
public class AppointmentMailtoResponse {

    @Schema(description = "ID del recurso MENTAL_HEALTH", example = "1")
    private String resourceId;

    @Schema(description = "Nombre de la psicóloga", example = "Dra. Andrea Gómez")
    private String psychologistName;

    @Schema(description = "Correo destino de la cita", example = "psicologia@escuelaing.edu.co")
    private String appointmentEmail;

    @Schema(description = "Asunto pre-armado del correo", example = "Solicitud de cita - Laura González")
    private String subject;

    @Schema(description = "Cuerpo pre-armado del correo con el nombre del estudiante inyectado",
            example = "Estimada Dra. Andrea Gómez, mi nombre es Laura González...")
    private String body;

    @Schema(description = "Enlace mailto listo para abrir en cliente de correo",
            example = "mailto:psicologia@escuelaing.edu.co?subject=Solicitud%20de%20cita&body=...")
    private String mailtoLink;
}