package edu.eci.patricia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single question–answer pair within a wellness survey submission (PTR23.1).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single answer to one of the ten wellness survey questions")
public class SurveyAnswerRequest {

    @NotBlank(message = "questionId is required")
    @Pattern(regexp = "P(0[1-9]|10)", message = "questionId must be P01–P10")
    @Schema(description = "Question identifier", example = "P01", pattern = "P(0[1-9]|10)")
    private String questionId;

    @NotBlank(message = "answer is required")
    @Schema(description = "Selected answer text", example = "Bien")
    private String answer;
}
