package edu.eci.patricia.bienestar.infrastructure.controller;

import edu.eci.patricia.bienestar.application.service.RecursoService;
import edu.eci.patricia.bienestar.domain.model.RecursoBienestar;
import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecursoController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecursoService recursoService;

    @Test
    @DisplayName("Como estudiante, debo poder acceder al endpoint de recursos sin error")
    void listarRecursos_sinFiltro_retorna200() throws Exception {
        when(recursoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/bienestar/recursos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Como estudiante, debo poder filtrar recursos por categoria CULTURA")
    void listarRecursos_filtradoPorCultura_retorna200() throws Exception {
        RecursoBienestar r = new RecursoBienestar();
        r.setId("1");
        r.setNombre("Coro universitario");
        r.setCategoria(TipoRecurso.CULTURA);
        r.setActivo(true);
        when(recursoService.obtenerPorCategoria(TipoRecurso.CULTURA)).thenReturn(List.of(r));

        mockMvc.perform(get("/api/v1/bienestar/recursos").param("categoria", "CULTURA"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Como estudiante, si envio una categoria invalida debo recibir un error 400")
    void listarRecursos_categoriaInvalida_retorna400() throws Exception {
        mockMvc.perform(get("/api/v1/bienestar/recursos").param("categoria", "INVALIDA"))
                .andExpect(status().isBadRequest());
    }
}
