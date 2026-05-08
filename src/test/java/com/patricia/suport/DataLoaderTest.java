package com.patricia.suport;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import edu.eci.patricia.infrastructure.config.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private JpaWellnessResourceRepository repository;

    @InjectMocks
    private DataLoader dataLoader;

    @BeforeEach
    void setUp() {
        // Por defecto la BD está vacía
        when(repository.count()).thenReturn(0L);
    }

    @Test
    void run_bdVacia_cargaLosOchoRecursos() throws Exception {
        dataLoader.run();

        ArgumentCaptor<List<WellnessResourceEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<WellnessResourceEntity> saved = captor.getValue();
        assertThat(saved).hasSize(8);
    }

    @Test
    void run_bdVacia_todosLosRecursosEsanActivos() throws Exception {
        dataLoader.run();

        ArgumentCaptor<List<WellnessResourceEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        assertThat(captor.getValue())
                .allMatch(WellnessResourceEntity::isActive);
    }

    @Test
    void run_bdVacia_incluyeUnRecursoDeMentalHealth() throws Exception {
        dataLoader.run();

        ArgumentCaptor<List<WellnessResourceEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        long mentalHealthCount = captor.getValue().stream()
                .filter(r -> r.getCategory() == WellnessCategory.MENTAL_HEALTH)
                .count();

        assertThat(mentalHealthCount).isEqualTo(1);
    }

    @Test
    void run_bdVacia_recursoMentalHealthTieneEmailYPsicologa() throws Exception {
        dataLoader.run();

        ArgumentCaptor<List<WellnessResourceEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        WellnessResourceEntity psicologia = captor.getValue().stream()
                .filter(r -> r.getCategory() == WellnessCategory.MENTAL_HEALTH)
                .findFirst()
                .orElseThrow();

        assertThat(psicologia.getAppointmentEmail()).isNotBlank();
        assertThat(psicologia.getPsychologistName()).isNotBlank();
    }

    @Test
    void run_bdYaTieneRecursos_noVuelveACargar() throws Exception {
        when(repository.count()).thenReturn(8L);

        dataLoader.run();

        verify(repository, never()).saveAll(any());
    }

    @Test
    void run_bdVacia_incluyeRecursosDeTodaLasCategorias() throws Exception {
        dataLoader.run();

        ArgumentCaptor<List<WellnessResourceEntity>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<WellnessResourceEntity> saved = captor.getValue();

        assertThat(saved).anyMatch(r -> r.getCategory() == WellnessCategory.MENTAL_HEALTH);
        assertThat(saved).anyMatch(r -> r.getCategory() == WellnessCategory.SPORTS);
        assertThat(saved).anyMatch(r -> r.getCategory() == WellnessCategory.CULTURE);
        assertThat(saved).anyMatch(r -> r.getCategory() == WellnessCategory.ACADEMIC_SUPPORT);
    }
}
