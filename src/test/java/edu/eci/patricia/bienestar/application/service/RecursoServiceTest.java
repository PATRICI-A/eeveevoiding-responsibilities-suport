package edu.eci.patricia.bienestar.application.service;

import edu.eci.patricia.bienestar.domain.model.RecursoBienestar;
import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import edu.eci.patricia.bienestar.domain.repository.RecursoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecursoServiceTest {

    @Mock
    private RecursoRepository recursoRepository;

    @InjectMocks
    private RecursoService recursoService;

    @Test
    @DisplayName("Como estudiante, debo poder ver todos los recursos de bienestar activos")
    void obtenerTodos_retornaListaCompleta() {
        RecursoBienestar r1 = recurso("1", "Psicología", TipoRecurso.SALUD_MENTAL);
        RecursoBienestar r2 = recurso("2", "Gimnasio", TipoRecurso.DEPORTES);
        when(recursoRepository.findByActivoTrueOrderByCategoria()).thenReturn(List.of(r1, r2));

        List<RecursoBienestar> resultado = recursoService.obtenerTodos();

        assertThat(resultado).hasSize(2).contains(r1, r2);
        verify(recursoRepository).findByActivoTrueOrderByCategoria();
    }

    @Test
    @DisplayName("Como estudiante, debo poder filtrar recursos por categoria SALUD_MENTAL")
    void obtenerPorCategoria_saludMental_retornaSoloEsaCategoria() {
        RecursoBienestar r1 = recurso("1", "Psicología", TipoRecurso.SALUD_MENTAL);
        when(recursoRepository.findByActivoTrueAndCategoria(TipoRecurso.SALUD_MENTAL)).thenReturn(List.of(r1));

        List<RecursoBienestar> resultado = recursoService.obtenerPorCategoria(TipoRecurso.SALUD_MENTAL);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategoria()).isEqualTo(TipoRecurso.SALUD_MENTAL);
        verify(recursoRepository).findByActivoTrueAndCategoria(TipoRecurso.SALUD_MENTAL);
    }

    @Test
    @DisplayName("Como estudiante, si no hay recursos disponibles, debo recibir una lista vacía")
    void obtenerTodos_sinRecursos_retornaListaVacia() {
        when(recursoRepository.findByActivoTrueOrderByCategoria()).thenReturn(List.of());

        List<RecursoBienestar> resultado = recursoService.obtenerTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Como estudiante, debo poder filtrar recursos por categoria DEPORTES")
    void obtenerPorCategoria_deportes_retornaSoloEsaCategoria() {
        RecursoBienestar r1 = recurso("2", "Gimnasio", TipoRecurso.DEPORTES);
        RecursoBienestar r2 = recurso("3", "Natación", TipoRecurso.DEPORTES);
        when(recursoRepository.findByActivoTrueAndCategoria(TipoRecurso.DEPORTES)).thenReturn(List.of(r1, r2));

        List<RecursoBienestar> resultado = recursoService.obtenerPorCategoria(TipoRecurso.DEPORTES);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(r -> r.getCategoria() == TipoRecurso.DEPORTES);
        verify(recursoRepository).findByActivoTrueAndCategoria(TipoRecurso.DEPORTES);
    }

    private RecursoBienestar recurso(String id, String nombre, TipoRecurso categoria) {
        RecursoBienestar r = new RecursoBienestar();
        r.setId(id);
        r.setNombre(nombre);
        r.setCategoria(categoria);
        r.setActivo(true);
        return r;
    }
}
