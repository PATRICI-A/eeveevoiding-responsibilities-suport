package com.patricia.suport;

import edu.eci.patricia.application.usecase.GetWellnessResourcesUseCaseImpl;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWellnessResourcesUseCaseImplTest {

    @Mock
    private WellnessResourceRepository repository;

    @InjectMocks
    private GetWellnessResourcesUseCaseImpl useCase;

    private WellnessResource sampleResource;

    @BeforeEach
    void setUp() {
        sampleResource = WellnessResource.builder()
                .id("1").name("Psicología").active(true)
                .category(WellnessCategory.MENTAL_HEALTH)
                .appointmentEmail("psi@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez")
                .build();
    }

    @Test
    void execute_sinFiltro_retornaTodosLosRecursos() {
        when(repository.findAllActive()).thenReturn(List.of(sampleResource));

        List<WellnessResource> result = useCase.execute(null);

        assertThat(result).hasSize(1);
        verify(repository).findAllActive();
        verify(repository, never()).findActiveByCategory(any());
    }

    @Test
    void execute_conFiltroMentalHealth_retornaRecursosFiltrados() {
        when(repository.findActiveByCategory(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(sampleResource));

        List<WellnessResource> result = useCase.execute(WellnessCategory.MENTAL_HEALTH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
        verify(repository).findActiveByCategory(WellnessCategory.MENTAL_HEALTH);
        verify(repository, never()).findAllActive();
    }

    @Test
    void execute_sinRecursos_retornaListaVacia() {
        when(repository.findAllActive()).thenReturn(List.of());

        List<WellnessResource> result = useCase.execute(null);

        assertThat(result).isEmpty();
    }
}