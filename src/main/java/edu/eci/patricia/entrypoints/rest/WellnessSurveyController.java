package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.application.dto.SurveyResultResponse;
import edu.eci.patricia.application.dto.SurveySubmissionRequest;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for student wellness survey operations.
 * Handles survey submission and retrieval of survey history with recommendations.
 */
@RestController
@RequestMapping("/api/v1/wellness/survey")
@RequiredArgsConstructor
@Tag(name = "Wellness Survey", description = "Endpoints for submitting and reviewing student wellness surveys")
@SecurityRequirement(name = "bearerAuth")
public class WellnessSurveyController {

    private final SubmitSurveyUseCase submitSurveyUseCase;
    private final GetRecommendationsUseCase recommendationsUseCase;

    /**
     * Submits a wellness survey for the authenticated student.
     * The student's UUID is extracted from the JWT subject claim.
     *
     * @param request        the survey submission payload
     * @param authentication the Spring Security authentication holding the student's userId as principal
     * @return the survey result including average score, wellbeing level, and recommendations
     */
    @PostMapping("/submit")
    @Operation(summary = "Submit a wellness survey",
               description = "Accepts a five-dimension wellness survey and returns the computed result with personalized resource recommendations.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Survey submitted and processed successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error — one or more scores are out of range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid JWT")
    })
    public ResponseEntity<SurveyResultResponse> submitSurvey(
            @Valid @RequestBody SurveySubmissionRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        SurveyResponse survey = SurveyResponse.builder()
                .userId(userId)
                .moodScore(request.getMoodScore())
                .stressScore(request.getStressScore())
                .sleepScore(request.getSleepScore())
                .socialScore(request.getSocialScore())
                .academicScore(request.getAcademicScore())
                .build();

        SurveyResponse saved = submitSurveyUseCase.submitSurvey(survey);

        List<WellnessResource> recommended = recommendationsUseCase.getRecommendations(
                request.getMoodScore(), request.getStressScore(),
                request.getSleepScore(), request.getSocialScore(), request.getAcademicScore());

        List<RecommendationResponse> recommendationResponses = recommended.stream()
                .map(this::toRecommendationResponse)
                .collect(Collectors.toList());

        SurveyResultResponse result = SurveyResultResponse.builder()
                .surveyId(saved.getId())
                .averageScore(saved.getAverageScore())
                .wellbeingLevel(saved.getWellbeingLevel())
                .submittedAt(saved.getSubmittedAt())
                .recommendations(recommendationResponses)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Returns the survey history for the authenticated student.
     * The student's UUID is extracted from the JWT subject claim.
     *
     * @param authentication the Spring Security authentication
     * @return list of past survey results for the authenticated student
     */
    @GetMapping("/history")
    @Operation(summary = "Get survey history",
               description = "Returns all wellness surveys previously submitted by the authenticated student.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Survey history retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid JWT")
    })
    public ResponseEntity<List<SurveyResultResponse>> getSurveyHistory(Authentication authentication) {
        UUID userId = UUID.fromString((String) authentication.getPrincipal());

        List<SurveyResultResponse> history = submitSurveyUseCase.getSurveyHistory(userId)
                .stream()
                .map(sr -> SurveyResultResponse.builder()
                        .surveyId(sr.getId())
                        .averageScore(sr.getAverageScore())
                        .wellbeingLevel(sr.getWellbeingLevel())
                        .submittedAt(sr.getSubmittedAt())
                        .recommendations(List.of())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private RecommendationResponse toRecommendationResponse(WellnessResource resource) {
        return RecommendationResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .category(resource.getCategory())
                .location(resource.getLocation())
                .contactInfo(resource.getContactInfo())
                .build();
    }
}
