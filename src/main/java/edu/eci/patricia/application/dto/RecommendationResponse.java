package edu.eci.patricia.application.dto;

import edu.eci.patricia.domain.model.WellnessCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO representing a single recommended wellness resource.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A recommended wellness resource based on the student survey results")
public class RecommendationResponse {

    @Schema(description = "Unique identifier of the recommended resource")
    private UUID id;

    @Schema(description = "Name of the recommended resource", example = "Psychological Counseling Center")
    private String name;

    @Schema(description = "Brief description of the resource", example = "Individual therapy sessions available weekdays")
    private String description;

    @Schema(description = "Wellness category of the resource", example = "MENTAL_HEALTH")
    private WellnessCategory category;

    @Schema(description = "Location of the resource", example = "Building A, Room 201")
    private String location;

    @Schema(description = "Contact information", example = "counseling@university.edu")
    private String contactInfo;
}
