package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting an inappropriate behavior report (RF24).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for submitting an inappropriate behavior report")
public class BehaviorReportRequest {

    @NotNull(message = "Report type is required")
    @Schema(description = "Classification of the behavior type",
            example = "HARASSMENT",
            allowableValues = {"HARASSMENT", "INAPPROPRIATE_BEHAVIOR", "OFFENSIVE_CONTENT"})
    private ReportType reportType;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Detailed description of the inappropriate behavior observed (max 1000 chars)",
            example = "A professor made offensive remarks during class")
    private String description;

    @Schema(description = "Optional ID (UUID) of the event or user involved", example = "550e8400-e29b-41d4-a716-446655440000")
    private String referenceId;
}
