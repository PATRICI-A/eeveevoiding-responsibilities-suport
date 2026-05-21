package edu.eci.patricia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response returned after a student submits a wellness survey (PTR23.1).
 * Contains the survey identifier, recommended categories and a confirmation message.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of a wellness survey submission including recommended categories")
public class SurveyResultResponse {

    @Schema(description = "UUID of the saved survey response")
    private String surveyId;

    @Schema(description = "Wellness categories recommended based on the survey answers")
    private List<RecommendedCategoryResponse> recommendedCategories;

    @Schema(description = "Confirmation message",
            example = "Encuesta registrada. Revisa tus recomendaciones de bienestar.")
    private String message;

    @Schema(description = "Timestamp when the survey was submitted")
    private LocalDateTime submittedAt;
}
