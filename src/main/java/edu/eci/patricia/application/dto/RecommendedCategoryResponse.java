package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one recommended category returned after survey submission (PTR23.2).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A wellness category recommended based on the student's survey answers")
public class RecommendedCategoryResponse {

    @Schema(description = "Recommended wellness category", example = "EMOTIONAL_SUPPORT")
    private WellnessCategory category;

    @Schema(description = "Number of conditions that triggered this recommendation", example = "2")
    private int score;

    @Schema(description = "Human-readable explanation for the recommendation",
            example = "Recomendado por nivel alto de estrés académico.")
    private String reason;
}
