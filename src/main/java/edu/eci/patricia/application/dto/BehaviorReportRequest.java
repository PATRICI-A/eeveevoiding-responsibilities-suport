package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting an inappropriate behavior report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for submitting an inappropriate behavior report")
public class BehaviorReportRequest {

    @NotBlank(message = "Description is required")
    @Schema(description = "Detailed description of the inappropriate behavior observed",
            example = "A professor made discriminatory remarks during class about students with disabilities")
    private String description;

    @Schema(description = "Physical location where the behavior occurred", example = "Main Auditorium, Block B")
    private String location;

    @NotNull(message = "Report type is required")
    @Schema(description = "Classification of the behavior type", example = "DISCRIMINATION")
    private ReportType reportType;
}
