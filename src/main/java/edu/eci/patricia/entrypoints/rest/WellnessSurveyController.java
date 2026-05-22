package edu.eci.patricia.entrypoints.rest;

import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.SurveyAnswerRequest;
import edu.eci.patricia.application.dto.SurveyResultResponse;
import edu.eci.patricia.application.dto.SurveySubmissionRequest;
import edu.eci.patricia.domain.model.SurveyResponse;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for wellness survey operations (PTR23.1 / PTR23.2).
 */
@RestController
@RequestMapping("/api/v1/bienestar/encuesta")
@RequiredArgsConstructor
@Tag(name = "Wellness Survey", description = "Encuesta de bienestar estudiantil (PTR23.1) y recomendaciones (PTR23.2)")
@SecurityRequirement(name = "bearerAuth")
public class WellnessSurveyController {

    /** All mandatory question IDs (P01–P10). */
    private static final Set<String> MANDATORY_QUESTIONS = Set.of(
            "P01", "P02", "P03", "P04", "P05",
            "P06", "P07", "P08", "P09", "P10");

    private final SubmitSurveyUseCase submitSurveyUseCase;
    private final GetRecommendationsUseCase recommendationsUseCase;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/bienestar/encuesta  — PTR23.1
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Submits a wellness survey for the authenticated student (PTR23.1).
     * Returns HTTP 201 with the survey ID and recommended categories.
     * Returns HTTP 400 if any of the ten mandatory questions are missing.
     */
    @PostMapping
    @Operation(summary = "Enviar encuesta de bienestar",
               description = "Recibe las 10 respuestas obligatorias (P01–P10), las persiste y retorna " +
                             "las categorías de recursos recomendadas (PTR23.1 / PTR23.2).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Encuesta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Una o más preguntas obligatorias sin respuesta"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido")
    })
    public ResponseEntity<SurveyResultResponse> submitSurvey(
            @Valid @RequestBody SurveySubmissionRequest request,
            Authentication authentication) {

        String studentId = (String) authentication.getPrincipal();

        // Validate that all mandatory questions are covered (RN-23.1.1)
        Set<String> submittedIds = request.getSurveyResponses().stream()
                .map(SurveyAnswerRequest::getQuestionId)
                .collect(Collectors.toSet());
        List<String> missingQuestions = MANDATORY_QUESTIONS.stream()
                .filter(q -> !submittedIds.contains(q))
                .sorted()
                .collect(Collectors.toList());
        if (!missingQuestions.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Build answers map
        Map<String, String> answers = request.getSurveyResponses().stream()
                .collect(Collectors.toMap(
                        SurveyAnswerRequest::getQuestionId,
                        SurveyAnswerRequest::getAnswer));

        SurveyResponse domain = SurveyResponse.builder()
                .studentId(studentId)
                .answers(answers)
                .build();

        SurveyResponse saved = submitSurveyUseCase.submitSurvey(domain);

        // Generate recommended categories (PTR23.2)
        List<RecommendedCategoryResponse> recommendedCategories =
                recommendationsUseCase.getRecommendedCategories(answers);

        SurveyResultResponse result = SurveyResultResponse.builder()
                .surveyId(saved.getId())
                .recommendedCategories(recommendedCategories)
                .submittedAt(saved.getSubmittedAt())
                .message("Encuesta registrada. Revisa tus recomendaciones de bienestar.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/bienestar/encuesta/preguntas  — PTR23.1 (question retrieval)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns the list of survey questions with their answer options (PTR23.1, step 2).
     */
    @GetMapping("/preguntas")
    @Operation(summary = "Obtener preguntas de la encuesta",
               description = "Retorna las 10 preguntas del cuestionario de bienestar con sus opciones de respuesta.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Preguntas retornadas exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido")
    })
    public ResponseEntity<List<SurveyQuestionDto>> getSurveyQuestions() {
        return ResponseEntity.ok(buildQuestions());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/bienestar/encuesta/recomendaciones  — PTR23.2
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns personalised wellness resource recommendations based on the student's
     * most recent survey (PTR23.2).  Falls back to all active resources if no survey
     * exists (RN-23.2.2).
     */
    @GetMapping("/recomendaciones")
    @Operation(summary = "Obtener recomendaciones de bienestar",
               description = "Retorna recursos activos recomendados según la última encuesta respondida (PTR23.2). " +
                             "Si el estudiante no ha respondido, retorna recursos generales (fallback).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recomendaciones retornadas exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token JWT ausente o inválido")
    })
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(Authentication authentication) {
        String studentId = (String) authentication.getPrincipal();
        List<RecommendationResponse> recommendations =
                recommendationsUseCase.getRecommendationsForStudent(studentId);
        return ResponseEntity.ok(recommendations);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Survey questions catalogue
    // ─────────────────────────────────────────────────────────────────────────

    private List<SurveyQuestionDto> buildQuestions() {
        return List.of(
            q("P01", "¿Cómo describirías tu estado de ánimo general esta semana?",
                    List.of("Muy bien", "Bien", "Regular", "Mal", "Muy mal")),
            q("P02", "¿Qué tan estresado/a te has sentido por tus responsabilidades académicas?",
                    List.of("1", "2", "3", "4", "5")),
            q("P03", "¿Con qué frecuencia realizas actividad física durante la semana?",
                    List.of("Todos los días", "3–4 veces", "1–2 veces", "Nunca")),
            q("P04", "¿Cómo calificarías la calidad de tu sueño últimamente?",
                    List.of("Muy buena", "Buena", "Regular", "Mala", "Muy mala")),
            q("P05", "¿Mantienes hábitos alimenticios regulares y saludables?",
                    List.of("Sí, siempre", "La mayoría de las veces", "A veces", "Casi nunca", "No")),
            q("P06", "¿Sientes que tienes apoyo social suficiente (amigos, familia, compañeros)?",
                    List.of("Sí, completamente", "En su mayoría sí", "No mucho", "No, me siento solo/a")),
            q("P07", "¿Participas en actividades culturales, sociales o recreativas del campus?",
                    List.of("Con frecuencia", "A veces", "Rara vez", "Nunca")),
            q("P08", "¿Qué tan satisfecho/a estás con tu rendimiento académico actual?",
                    List.of("1", "2", "3", "4", "5")),
            q("P09", "¿Has experimentado síntomas físicos como fatiga, dolor de cabeza o tensión muscular?",
                    List.of("Nunca", "A veces", "Con frecuencia", "Casi siempre")),
            q("P10", "¿En qué áreas crees que necesitas más apoyo? (puedes elegir varias)",
                    List.of("Actividad física", "Alimentación", "Salud física",
                            "Manejo del estrés", "Apoyo emocional", "Integración social", "Ninguna"))
        );
    }

    private SurveyQuestionDto q(String id, String text, List<String> options) {
        return new SurveyQuestionDto(id, text, options);
    }

    /** Simple DTO for question data. */
    public record SurveyQuestionDto(String questionId, String questionText, List<String> options) {}
}
