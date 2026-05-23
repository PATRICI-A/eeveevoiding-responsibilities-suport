package edu.eci.patricia.entrypoints.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.eci.patricia.application.dto.RecommendedCategoryResponse;
import edu.eci.patricia.application.dto.RecommendationResponse;
import edu.eci.patricia.application.dto.SurveyAnswerRequest;
import edu.eci.patricia.application.dto.SurveySubmissionRequest;
import edu.eci.patricia.domain.model.SurveyResponse;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetRecommendationsUseCase;
import edu.eci.patricia.domain.ports.in.SubmitSurveyUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:surveytestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-for-ci-only-at-least-32-chars"
})
class WellnessSurveyControllerTest {

    private static final String JWT_SECRET = "test-secret-for-ci-only-at-least-32-chars";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private SubmitSurveyUseCase submitSurveyUseCase;

    @MockitoBean
    private GetRecommendationsUseCase recommendationsUseCase;

    private String bearerToken;
    private UUID userId;
    private SurveySubmissionRequest validRequest;
    private SurveyResponse savedSurvey;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        userId = UUID.randomUUID();
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        bearerToken = "Bearer " + Jwts.builder()
                .subject(userId.toString())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        List<SurveyAnswerRequest> answers = List.of(
                new SurveyAnswerRequest("P01", "Bien"),
                new SurveyAnswerRequest("P02", "3"),
                new SurveyAnswerRequest("P03", "Todos los días"),
                new SurveyAnswerRequest("P04", "Buena"),
                new SurveyAnswerRequest("P05", "Sí, siempre"),
                new SurveyAnswerRequest("P06", "Sí, completamente"),
                new SurveyAnswerRequest("P07", "Con frecuencia"),
                new SurveyAnswerRequest("P08", "4"),
                new SurveyAnswerRequest("P09", "A veces"),
                new SurveyAnswerRequest("P10", "Ninguna")
        );
        validRequest = SurveySubmissionRequest.builder()
                .surveyResponses(answers)
                .build();

        savedSurvey = SurveyResponse.builder()
                .id(UUID.randomUUID().toString())
                .studentId(userId.toString())
                .answers(Map.of("P01", "Bien", "P02", "3"))
                .submittedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/bienestar/encuesta returns 201 with result")
    void submitSurvey_valid_returns201() throws Exception {
        when(submitSurveyUseCase.submitSurvey(any(SurveyResponse.class))).thenReturn(savedSurvey);
        when(recommendationsUseCase.getRecommendedCategories(any())).thenReturn(List.of(
                RecommendedCategoryResponse.builder()
                        .category(WellnessCategory.HEALTH)
                        .score(1)
                        .reason("Test reason")
                        .build()
        ));

        mockMvc.perform(post("/api/v1/bienestar/encuesta")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.surveyId").value(savedSurvey.getId()))
                .andExpect(jsonPath("$.message").value("Encuesta registrada. Revisa tus recomendaciones de bienestar."));
    }

    @Test
    @DisplayName("POST /api/v1/bienestar/encuesta returns 201 with recommendations")
    void submitSurvey_withRecommendations_includesThemInResponse() throws Exception {
        when(submitSurveyUseCase.submitSurvey(any(SurveyResponse.class))).thenReturn(savedSurvey);
        when(recommendationsUseCase.getRecommendedCategories(any())).thenReturn(List.of(
                RecommendedCategoryResponse.builder()
                        .category(WellnessCategory.EMOTIONAL_SUPPORT)
                        .score(2)
                        .reason("Recommended for emotional support")
                        .build()
        ));

        mockMvc.perform(post("/api/v1/bienestar/encuesta")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recommendedCategories[0].category").value("EMOTIONAL_SUPPORT"))
                .andExpect(jsonPath("$.recommendedCategories[0].score").value(2))
                .andExpect(jsonPath("$.recommendedCategories[0].reason").value("Recommended for emotional support"));
    }

    @Test
    @DisplayName("POST /api/v1/bienestar/encuesta returns 400 when questions missing")
    void submitSurvey_missingQuestions_returns400() throws Exception {
        SurveySubmissionRequest invalid = SurveySubmissionRequest.builder()
                .surveyResponses(List.of(new SurveyAnswerRequest("P01", "Bien")))
                .build();

        mockMvc.perform(post("/api/v1/bienestar/encuesta")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/bienestar/encuesta without auth returns 401")
    void submitSurvey_noAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/bienestar/encuesta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/bienestar/encuesta/preguntas returns 200 with questions")
    void getSurveyQuestions_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/bienestar/encuesta/preguntas")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].questionId").value("P01"))
                .andExpect(jsonPath("$[0].questionText").isNotEmpty())
                .andExpect(jsonPath("$[0].options").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/bienestar/encuesta/recomendaciones returns 200 with recommendations")
    void getRecommendations_returns200() throws Exception {
        WellnessResource resource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Counseling Center")
                .description("Therapy services")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .available(true)
                .build();

        when(recommendationsUseCase.getRecommendationsForStudent(userId.toString()))
                .thenReturn(List.of(
                        RecommendationResponse.builder()
                                .id(resource.getId().toString())
                                .name("Counseling Center")
                                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                                .recommendationReason("Recomendado por estrés")
                                .build()
                ));

        mockMvc.perform(get("/api/v1/bienestar/encuesta/recomendaciones")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Counseling Center"))
                .andExpect(jsonPath("$[0].recommendationReason").value("Recomendado por estrés"));
    }

    @Test
    @DisplayName("GET /api/v1/bienestar/encuesta/recomendaciones returns empty list when none")
    void getRecommendations_empty_returnsEmptyList() throws Exception {
        when(recommendationsUseCase.getRecommendationsForStudent(userId.toString()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bienestar/encuesta/recomendaciones")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
