package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A single recommended wellness resource returned to the student (PTR23 / PTR23.2).
 * Includes the reason it was recommended when coming from the RECOMMENDATIONS filter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A wellness resource recommended based on the student's survey results")
public class RecommendationResponse {

    @Schema(description = "Unique identifier of the resource")
    private String id;

    @Schema(description = "Name of the resource", example = "Servicio de Psicología")
    private String name;

    @Schema(description = "Brief description of the resource")
    private String description;

    @Schema(description = "Wellness category", example = "EMOTIONAL_SUPPORT")
    private WellnessCategory category;

    @Schema(description = "Location on campus", example = "Bloque A, Piso 2")
    private String location;

    @Schema(description = "Contact information")
    private String contactInfo;

    @Schema(description = "Attendance schedule", example = "Lunes a viernes, 8:00 a.m. – 5:00 p.m.")
    private String schedule;

    /**
     * Only populated when the resource comes from the RECOMMENDATIONS filter (PTR23.2 / RN-23.2.5).
     */
    @Schema(description = "Reason this resource was recommended",
            example = "Recomendado por nivel alto de estrés académico.",
            nullable = true)
    private String recommendationReason;
}
