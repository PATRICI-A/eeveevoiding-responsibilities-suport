package com.patricia.suport;

import edu.eci.patricia.application.dto.response.AppointmentMailtoResponse;
import edu.eci.patricia.application.dto.response.WellnessResourceResponse;
import edu.eci.patricia.application.mapper.WellnessMapper;
import edu.eci.patricia.domain.exceptions.InvalidCategoryForMailtoException;
import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.AppointmentMailto;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetAppointmentMailtoUseCase;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import edu.eci.patricia.entrypoints.advice.WellnessExceptionHandler;
import edu.eci.patricia.entrypoints.rest.controller.WellnessController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WellnessController.class)
@Import(WellnessExceptionHandler.class)
class WellnessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetWellnessResourcesUseCase getWellnessResourcesUseCase;

    @MockBean
    private GetAppointmentMailtoUseCase getAppointmentMailtoUseCase;

    @MockBean
    private WellnessMapper mapper;

    private WellnessResource mentalHealthResource;
    private WellnessResource sportsResource;
    private WellnessResourceResponse mentalHealthResponse;
    private WellnessResourceResponse sportsResponse;

    /** Simula el Authentication que pone JwtAuthenticationFilter en el SecurityContext */
    private UsernamePasswordAuthenticationToken authLaura;

    @BeforeEach
    void setUp() {
        mentalHealthResource = WellnessResource.builder()
                .id("1").name("Psicología")
                .category(WellnessCategory.MENTAL_HEALTH).active(true)
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez").build();

        sportsResource = WellnessResource.builder()
                .id("2").name("Canchas")
                .category(WellnessCategory.SPORTS).active(true).build();

        mentalHealthResponse = WellnessResourceResponse.builder()
                .id("1").name("Psicología")
                .category(WellnessCategory.MENTAL_HEALTH)
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez").build();

        sportsResponse = WellnessResourceResponse.builder()
                .id("2").name("Canchas")
                .category(WellnessCategory.SPORTS).build();

        // Authentication equivalente al que produce JwtAuthenticationFilter
        authLaura = new UsernamePasswordAuthenticationToken(
                "user-123", null,
                List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"))
        );
        authLaura.setDetails("Laura González"); // userName desde el claim del JWT
    }

    // ── GET /api/v1/bienestar/recursos ────────────────────────────────────────

    @Test
    void getResources_sinFiltro_retorna200ConTodosLosRecursos() throws Exception {
        when(getWellnessResourcesUseCase.execute(null))
                .thenReturn(List.of(mentalHealthResource, sportsResource));
        when(mapper.toResponse(mentalHealthResource)).thenReturn(mentalHealthResponse);
        when(mapper.toResponse(sportsResource)).thenReturn(sportsResponse);

        mockMvc.perform(get("/api/v1/bienestar/recursos")
                        .with(authentication(authLaura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }

    @Test
    void getResources_conFiltroMentalHealth_retorna200ConCamposDeCita() throws Exception {
        when(getWellnessResourcesUseCase.execute(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(mentalHealthResource));
        when(mapper.toResponse(mentalHealthResource)).thenReturn(mentalHealthResponse);

        mockMvc.perform(get("/api/v1/bienestar/recursos")
                        .with(authentication(authLaura))
                        .param("categoryFilter", "MENTAL_HEALTH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("MENTAL_HEALTH"))
                .andExpect(jsonPath("$[0].appointmentEmail").value("psicologia@escuelaing.edu.co"))
                .andExpect(jsonPath("$[0].psychologistName").value("Dra. Andrea Gómez"));
    }

    @Test
    void getResources_sinRecursos_retorna200ListaVacia() throws Exception {
        when(getWellnessResourcesUseCase.execute(null)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bienestar/recursos")
                        .with(authentication(authLaura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getResources_categoriaInvalida_retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/bienestar/recursos")
                        .with(authentication(authLaura))
                        .param("categoryFilter", "CATEGORIA_INVALIDA"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getResources_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/bienestar/recursos"))
                .andExpect(status().isUnauthorized());
    }

    // ── GET /api/v1/bienestar/recursos/{id}/cita-mailto ───────────────────────

    @Test
    void getCitaMailto_recursoMentalHealth_retorna200ConMailtoLink() throws Exception {
        AppointmentMailto mailto = AppointmentMailto.builder()
                .resourceId("1").psychologistName("Dra. Andrea Gómez")
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .subject("Solicitud de cita - Laura González")
                .body("Estimada Dra. Andrea Gómez, mi nombre es Laura González...")
                .mailtoLink("mailto:psicologia@escuelaing.edu.co?subject=Solicitud&body=...").build();

        AppointmentMailtoResponse mailtoResponse = AppointmentMailtoResponse.builder()
                .resourceId("1").psychologistName("Dra. Andrea Gómez")
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .subject("Solicitud de cita - Laura González")
                .body("Estimada Dra. Andrea Gómez, mi nombre es Laura González...")
                .mailtoLink("mailto:psicologia@escuelaing.edu.co?subject=Solicitud&body=...").build();

        // El userName viene de authentication.getDetails() → "Laura González"
        when(getAppointmentMailtoUseCase.execute("1", "Laura González")).thenReturn(mailto);
        when(mapper.toMailtoResponse(mailto)).thenReturn(mailtoResponse);

        mockMvc.perform(get("/api/v1/bienestar/recursos/1/cita-mailto")
                        .with(authentication(authLaura)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value("1"))
                .andExpect(jsonPath("$.mailtoLink", startsWith("mailto:")))
                .andExpect(jsonPath("$.subject", containsString("Laura González")))
                .andExpect(jsonPath("$.body", containsString("Laura González")));
    }

    @Test
    void getCitaMailto_recursoNoMentalHealth_retorna400() throws Exception {
        when(getAppointmentMailtoUseCase.execute(eq("2"), any()))
                .thenThrow(new InvalidCategoryForMailtoException("2"));

        mockMvc.perform(get("/api/v1/bienestar/recursos/2/cita-mailto")
                        .with(authentication(authLaura)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("MENTAL_HEALTH")));
    }

    @Test
    void getCitaMailto_recursoNoEncontrado_retorna404() throws Exception {
        when(getAppointmentMailtoUseCase.execute(eq("999"), any()))
                .thenThrow(new ResourceNotFoundException("999"));

        mockMvc.perform(get("/api/v1/bienestar/recursos/999/cita-mailto")
                        .with(authentication(authLaura)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getCitaMailto_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(get("/api/v1/bienestar/recursos/1/cita-mailto"))
                .andExpect(status().isUnauthorized());
    }
}
