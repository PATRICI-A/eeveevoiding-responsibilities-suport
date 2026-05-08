package com.patricia.suport;

import edu.eci.patricia.application.usecase.GetAppointmentMailtoUseCaseImpl;
import edu.eci.patricia.domain.exceptions.InvalidCategoryForMailtoException;
import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.AppointmentMailto;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAppointmentMailtoUseCaseImplTest {

    @Mock
    private WellnessResourceRepository repository;

    @InjectMocks
    private GetAppointmentMailtoUseCaseImpl useCase;

    private WellnessResource mentalHealthResource;
    private WellnessResource sportsResource;

    @BeforeEach
    void setUp() {
        mentalHealthResource = WellnessResource.builder()
                .id("1").name("Psicología").active(true)
                .category(WellnessCategory.MENTAL_HEALTH)
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez")
                .build();

        sportsResource = WellnessResource.builder()
                .id("2").name("Canchas").active(true)
                .category(WellnessCategory.SPORTS)
                .build();
    }

    @Test
    void execute_recursoMentalHealth_generaMailtoConNombreEstudiante() {
        when(repository.findById("1")).thenReturn(Optional.of(mentalHealthResource));

        AppointmentMailto result = useCase.execute("1", "Laura González");

        assertThat(result.getMailtoLink()).startsWith("mailto:psicologia@escuelaing.edu.co");
        assertThat(result.getBody()).contains("Laura González");
        assertThat(result.getSubject()).contains("Laura González");
        assertThat(result.getPsychologistName()).isEqualTo("Dra. Andrea Gómez");
    }

    @Test
    void execute_recursoNoMentalHealth_lanzaInvalidCategoryException() {
        when(repository.findById("2")).thenReturn(Optional.of(sportsResource));

        assertThatThrownBy(() -> useCase.execute("2", "Laura González"))
                .isInstanceOf(InvalidCategoryForMailtoException.class);
    }

    @Test
    void execute_recursoNoEncontrado_lanzaResourceNotFoundException() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("999", "Laura González"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}