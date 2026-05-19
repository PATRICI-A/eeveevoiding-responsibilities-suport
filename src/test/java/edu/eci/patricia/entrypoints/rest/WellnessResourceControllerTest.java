package edu.eci.patricia.entrypoints.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.eci.patricia.application.dto.WellnessResourceRequest;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import edu.eci.patricia.domain.ports.in.ManageWellnessResourceUseCase;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link WellnessResourceController} with full Spring Security context.
 * Uses real JWT tokens so the JwtAuthFilter can authenticate requests correctly.
 * Service layer is mocked to focus on controller logic.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:resourcetestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "jwt.secret=test-secret-for-ci-only-at-least-32-chars"
})
class WellnessResourceControllerTest {

    private static final String JWT_SECRET = "test-secret-for-ci-only-at-least-32-chars";

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private GetWellnessResourcesUseCase getResourcesUseCase;

    @MockitoBean
    private ManageWellnessResourceUseCase manageResourceUseCase;

    private String bearerToken;
    private UUID resourceId;
    private WellnessResource sampleResource;
    private WellnessResourceRequest sampleRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        UUID userId = UUID.randomUUID();
        SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        bearerToken = "Bearer " + Jwts.builder()
                .subject(userId.toString())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        resourceId = UUID.randomUUID();
        sampleResource = WellnessResource.builder()
                .id(resourceId)
                .name("Counseling Center")
                .description("Individual and group therapy sessions")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building A, Room 101")
                .contactInfo("counseling@eci.edu.co")
                .schedule("Mon-Fri 08:00-17:00")
                .available(true)
                .createdAt(LocalDateTime.now())
                .build();

        sampleRequest = WellnessResourceRequest.builder()
                .name("Counseling Center")
                .description("Individual and group therapy sessions")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building A, Room 101")
                .contactInfo("counseling@eci.edu.co")
                .schedule("Mon-Fri 08:00-17:00")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/wellness/resources returns 200 with all resources")
    void getAllResources_noFilter_returns200() throws Exception {
        when(getResourcesUseCase.getAllResources(null)).thenReturn(List.of(sampleResource));

        mockMvc.perform(get("/api/v1/wellness/resources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Counseling Center"))
                .andExpect(jsonPath("$[0].category").value("MENTAL_HEALTH"));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/resources?category=MENTAL_HEALTH returns filtered list")
    void getAllResources_withCategory_returnsFiltered() throws Exception {
        when(getResourcesUseCase.getAllResources(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(sampleResource));

        mockMvc.perform(get("/api/v1/wellness/resources")
                        .param("category", "MENTAL_HEALTH")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("MENTAL_HEALTH"));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/resources/{id} returns 200 when found")
    void getResourceById_found_returns200() throws Exception {
        when(getResourcesUseCase.getResourceById(resourceId)).thenReturn(Optional.of(sampleResource));

        mockMvc.perform(get("/api/v1/wellness/resources/{id}", resourceId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId.toString()))
                .andExpect(jsonPath("$.name").value("Counseling Center"));
    }

    @Test
    @DisplayName("GET /api/v1/wellness/resources/{id} returns 404 when not found")
    void getResourceById_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(getResourcesUseCase.getResourceById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/wellness/resources/{id}", unknownId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/wellness/resources returns 201 when valid")
    void createResource_valid_returns201() throws Exception {
        when(manageResourceUseCase.createResource(any(WellnessResource.class))).thenReturn(sampleResource);

        mockMvc.perform(post("/api/v1/wellness/resources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Counseling Center"));
    }

    @Test
    @DisplayName("POST /api/v1/wellness/resources returns 400 on validation error")
    void createResource_missingName_returns400() throws Exception {
        WellnessResourceRequest invalid = WellnessResourceRequest.builder()
                .description("A description")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Room 101")
                .build();

        mockMvc.perform(post("/api/v1/wellness/resources")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/wellness/resources/{id} returns 200 when resource exists")
    void updateResource_exists_returns200() throws Exception {
        when(manageResourceUseCase.updateResource(eq(resourceId), any(WellnessResource.class)))
                .thenReturn(sampleResource);

        mockMvc.perform(put("/api/v1/wellness/resources/{id}", resourceId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resourceId.toString()));
    }

    @Test
    @DisplayName("PUT /api/v1/wellness/resources/{id} returns 404 when resource not found")
    void updateResource_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(manageResourceUseCase.updateResource(eq(unknownId), any(WellnessResource.class)))
                .thenThrow(new ResourceNotFoundException("Wellness resource not found with id: " + unknownId));

        mockMvc.perform(put("/api/v1/wellness/resources/{id}", unknownId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/wellness/resources/{id} returns 204 when resource exists")
    void deleteResource_exists_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/wellness/resources/{id}", resourceId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/wellness/resources/{id} returns 404 when resource not found")
    void deleteResource_notFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("not found")).when(manageResourceUseCase).deleteResource(unknownId);

        mockMvc.perform(delete("/api/v1/wellness/resources/{id}", unknownId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/wellness/resources without auth returns 401")
    void getAllResources_noAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/wellness/resources"))
                .andExpect(status().isUnauthorized());
    }
}
