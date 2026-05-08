package edu.eci.patricia;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.adapter.WellnessResourceRepositoryAdapter;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourcePersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WellnessResourceRepositoryAdapterTest {

    @Mock
    private JpaWellnessResourceRepository jpaRepository;

    @Mock
    private WellnessResourcePersistenceMapper persistenceMapper;

    @InjectMocks
    private WellnessResourceRepositoryAdapter adapter;

    private WellnessResourceEntity entityMentalHealth;
    private WellnessResourceEntity entitySports;
    private WellnessResource domainMentalHealth;
    private WellnessResource domainSports;

    @BeforeEach
    void setUp() {
        entityMentalHealth = WellnessResourceEntity.builder()
                .id("1").name("Psicología")
                .category(WellnessCategory.MENTAL_HEALTH).active(true)
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez").build();

        entitySports = WellnessResourceEntity.builder()
                .id("2").name("Canchas")
                .category(WellnessCategory.SPORTS).active(true).build();

        domainMentalHealth = WellnessResource.builder()
                .id("1").name("Psicología")
                .category(WellnessCategory.MENTAL_HEALTH).active(true)
                .appointmentEmail("psicologia@escuelaing.edu.co")
                .psychologistName("Dra. Andrea Gómez").build();

        domainSports = WellnessResource.builder()
                .id("2").name("Canchas")
                .category(WellnessCategory.SPORTS).active(true).build();
    }

    @Test
    void findAllActive_retornaTodosLosRecursosActivos() {
        // Método real: findAllByActiveTrue()  ← corregido
        when(jpaRepository.findAllByActiveTrue()).thenReturn(List.of(entityMentalHealth, entitySports));
        when(persistenceMapper.toDomain(entityMentalHealth)).thenReturn(domainMentalHealth);
        when(persistenceMapper.toDomain(entitySports)).thenReturn(domainSports);

        List<WellnessResource> result = adapter.findAllActive();

        assertThat(result).hasSize(2);
        verify(jpaRepository).findAllByActiveTrue();   // ← corregido
    }

    @Test
    void findAllActive_retornaListaVaciaSiNoHayRecursos() {
        when(jpaRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<WellnessResource> result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    void findActiveByCategory_retornaSoloCategoriaIndicada() {
        // Método real: findAllByActiveTrueAndCategory()  ← corregido
        when(jpaRepository.findAllByActiveTrueAndCategory(WellnessCategory.MENTAL_HEALTH))
                .thenReturn(List.of(entityMentalHealth));
        when(persistenceMapper.toDomain(entityMentalHealth)).thenReturn(domainMentalHealth);

        List<WellnessResource> result = adapter.findActiveByCategory(WellnessCategory.MENTAL_HEALTH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
    }

    @Test
    void findActiveByCategory_sinResultados_retornaListaVacia() {
        when(jpaRepository.findAllByActiveTrueAndCategory(WellnessCategory.CULTURE))
                .thenReturn(List.of());

        List<WellnessResource> result = adapter.findActiveByCategory(WellnessCategory.CULTURE);

        assertThat(result).isEmpty();
    }

    @Test
    void findById_idValido_retornaRecurso() {
        when(jpaRepository.findById("1")).thenReturn(Optional.of(entityMentalHealth));
        when(persistenceMapper.toDomain(entityMentalHealth)).thenReturn(domainMentalHealth);

        Optional<WellnessResource> result = adapter.findById("1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
    }

    @Test
    void findById_idInvalido_retornaVacio() {
        when(jpaRepository.findById("999")).thenReturn(Optional.empty());

        Optional<WellnessResource> result = adapter.findById("999");

        assertThat(result).isEmpty();
    }
}