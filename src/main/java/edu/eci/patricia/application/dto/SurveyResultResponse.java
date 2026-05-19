package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellbeingLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO returned after a student submits a wellness survey.
 * Includes computed scores, wellbeing classification, and recommended resources.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of a wellness survey submission including recommendations")
public class SurveyResultResponse {

    @Schema(description = "Unique identifier of the saved survey response")
    private UUID surveyId;

    @Schema(description = "Average of all five dimension scores", example = "3.4")
    private double averageScore;

    @Schema(description = "Wellbeing classification derived from the average score", example = "MODERATE")
    private WellbeingLevel wellbeingLevel;

    @Schema(description = "Timestamp when the survey was submitted")
    private LocalDateTime submittedAt;

    @Schema(description = "List of recommended wellness resources based on low dimension scores")
    private List<RecommendationResponse> recommendations;
}
