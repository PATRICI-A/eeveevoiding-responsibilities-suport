package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a wellness resource returned to the client (RF23).
 * For EMOTIONAL_SUPPORT resources, appointmentEmail and psychologistName are populated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wellness resource details returned by the API")
public class WellnessResourceResponse {

    @Schema(description = "Unique identifier of the resource", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Display name of the resource", example = "Psychological Counseling Center")
    private String name;

    @Schema(description = "Detailed description of services", example = "Individual therapy sessions")
    private String description;

    @Schema(description = "Wellness category", example = "EMOTIONAL_SUPPORT")
    private WellnessCategory category;

    @Schema(description = "Physical or virtual location", example = "Building A, Room 201")
    private String location;

    @Schema(description = "Contact information", example = "counseling@eci.edu.co")
    private String contactInfo;

    @Schema(description = "Operating schedule", example = "Mon-Fri 08:00-17:00")
    private String schedule;

    @Schema(description = "Whether the resource is currently available", example = "true")
    private boolean available;

    @Schema(description = "Psychologist email — only for EMOTIONAL_SUPPORT resources", example = "psicologia@eci.edu.co")
    private String appointmentEmail;

    @Schema(description = "Psychologist name — only for EMOTIONAL_SUPPORT resources", example = "Dra. María García")
    private String psychologistName;

    @Schema(description = "Timestamp when the resource was created")
    private LocalDateTime createdAt;
}
