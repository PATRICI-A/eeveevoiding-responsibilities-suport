package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a wellness resource (RF23).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for creating or updating a wellness resource")
public class WellnessResourceRequest {

    @NotBlank(message = "Name is required")
    @Schema(description = "Display name of the wellness resource", example = "Psychological Counseling Center")
    private String name;

    @NotBlank(message = "Description is required")
    @Schema(description = "Detailed description of the services provided",
            example = "Individual and group therapy sessions available Monday through Friday")
    private String description;

    @NotNull(message = "Category is required")
    @Schema(description = "Wellness category this resource belongs to",
            example = "EMOTIONAL_SUPPORT",
            allowableValues = {"EMOTIONAL_SUPPORT", "SPORTS", "CULTURE", "HEALTH"})
    private WellnessCategory category;

    @NotBlank(message = "Location is required")
    @Schema(description = "Physical or virtual location", example = "Building A, Room 201")
    private String location;

    @Schema(description = "Contact email, phone, or website", example = "counseling@eci.edu.co")
    private String contactInfo;

    @Schema(description = "Operating schedule", example = "Mon-Fri 08:00-17:00")
    private String schedule;

    @Schema(description = "Whether the resource is currently available", example = "true")
    private boolean available;

    @Schema(description = "Psychologist email address — only for EMOTIONAL_SUPPORT resources",
            example = "psicologia@eci.edu.co")
    private String appointmentEmail;

    @Schema(description = "Psychologist name — only for EMOTIONAL_SUPPORT resources",
            example = "Dra. María García")
    private String psychologistName;
}
