package edu.eci.patricia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO containing the pre-built mailto components for requesting a psychological appointment (RF23 HU-23-03).
 * The client opens {@code appointmentEmailTo + appointmentEmailSubject + appointmentEmailBody}
 * as a mailto link in the device's email app. The system does NOT send the email directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pre-built mailto components for requesting a psychological appointment")
public class AppointmentMailtoResponse {

    @Schema(description = "Recipient email address (psychologist)", example = "psicologia@eci.edu.co")
    private String appointmentEmailTo;

    @Schema(description = "Pre-filled email subject", example = "Solicitud de cita psicológica - PATRICI.A")
    private String appointmentEmailSubject;

    @Schema(description = "Pre-filled email body with student identification injected from JWT")
    private String appointmentEmailBody;
}
