package edu.eci.patricia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for submitting a student wellness survey (PTR23.1).
 * All ten questions P01–P10 must be answered.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Wellness survey submission payload with answers to the ten mandatory questions")
public class SurveySubmissionRequest {

    @NotNull(message = "surveyResponses is required")
    @NotEmpty(message = "All ten survey questions must be answered")
    @Valid
    @Schema(description = "List of answers to the ten survey questions (P01–P10)")
    private List<SurveyAnswerRequest> surveyResponses;
}
