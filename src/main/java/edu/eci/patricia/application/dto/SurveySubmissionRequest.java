package edu.eci.patricia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for submitting a student wellness survey.
 * Each dimension is scored on a scale of 1 (worst) to 5 (best).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wellness survey submission payload with scores for five dimensions")
public class SurveySubmissionRequest {

    @NotNull(message = "Mood score is required")
    @Min(value = 1, message = "Mood score must be at least 1")
    @Max(value = 5, message = "Mood score must be at most 5")
    @Schema(description = "Self-reported mood score (1=very bad, 5=excellent)", example = "4", minimum = "1", maximum = "5")
    private Integer moodScore;

    @NotNull(message = "Stress score is required")
    @Min(value = 1, message = "Stress score must be at least 1")
    @Max(value = 5, message = "Stress score must be at most 5")
    @Schema(description = "Self-reported stress score (1=extremely stressed, 5=relaxed)", example = "2", minimum = "1", maximum = "5")
    private Integer stressScore;

    @NotNull(message = "Sleep score is required")
    @Min(value = 1, message = "Sleep score must be at least 1")
    @Max(value = 5, message = "Sleep score must be at most 5")
    @Schema(description = "Self-reported sleep quality score (1=very poor, 5=excellent)", example = "3", minimum = "1", maximum = "5")
    private Integer sleepScore;

    @NotNull(message = "Social score is required")
    @Min(value = 1, message = "Social score must be at least 1")
    @Max(value = 5, message = "Social score must be at most 5")
    @Schema(description = "Self-reported social connection score (1=isolated, 5=very connected)", example = "4", minimum = "1", maximum = "5")
    private Integer socialScore;

    @NotNull(message = "Academic score is required")
    @Min(value = 1, message = "Academic score must be at least 1")
    @Max(value = 5, message = "Academic score must be at most 5")
    @Schema(description = "Self-reported academic performance score (1=struggling, 5=thriving)", example = "3", minimum = "1", maximum = "5")
    private Integer academicScore;
}
