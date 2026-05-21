package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessResourceRepositoryAdapterTest {

    @Mock
    private JpaWellnessResourceRepository jpaRepository;

    @InjectMocks
    private WellnessResourceRepositoryAdapter adapter;

    private UUID resourceId;
    private WellnessResourceEntity entityResource;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();

        entityResource = WellnessResourceEntity.builder()
                .id(resourceId.toString())
                .name("Sports Center")
                .description("Daily fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Wellness Complex")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("findAllActive returns list of domain resources")
    void findAllActive_returnsMappedDomainList() {
        when(jpaRepository.findByAvailableTrue()).thenReturn(List.of(entityResource));

        List<WellnessResource> result = adapter.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sports Center");
        assertThat(result.get(0).getId()).isEqualTo(resourceId);
    }

    @Test
    @DisplayName("findAllActive returns empty list when none active")
    void findAllActive_noActive_returnsEmpty() {
        when(jpaRepository.findByAvailableTrue()).thenReturn(List.of());

        List<WellnessResource> result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findActiveByCategory returns filtered domain resources")
    void findActiveByCategory_returnsFilteredList() {
        when(jpaRepository.findByAvailableTrueAndCategory(WellnessCategory.SPORTS))
                .thenReturn(List.of(entityResource));

        List<WellnessResource> result = adapter.findActiveByCategory(WellnessCategory.SPORTS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("findById returns Optional with domain resource when found and active")
    void findById_found_returnsOptional() {
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.of(entityResource));

        Optional<WellnessResource> result = adapter.findById(resourceId.toString());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(resourceId);
    }

    @Test
    @DisplayName("findById returns empty when not found")
    void findById_notFound_returnsEmpty() {
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.empty());

        Optional<WellnessResource> result = adapter.findById(resourceId.toString());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById filters out inactive resources")
    void findById_inactive_returnsEmpty() {
        entityResource.setAvailable(false);
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.of(entityResource));

        Optional<WellnessResource> result = adapter.findById(resourceId.toString());

        assertThat(result).isEmpty();
    }
}
