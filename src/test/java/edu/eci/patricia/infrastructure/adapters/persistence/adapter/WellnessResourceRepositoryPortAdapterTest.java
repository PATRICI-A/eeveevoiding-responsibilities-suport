package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WellnessResourceRepositoryPortAdapterTest {

    @Mock
    private JpaWellnessResourceRepository jpaRepository;

    @InjectMocks
    private WellnessResourceRepositoryPortAdapter adapter;

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
                .contactInfo("555-0100")
                .schedule("Mon-Fri 6am-9pm")
                .available(true)
                .appointmentEmail("sports@university.edu")
                .psychologistName("Dr. Smith")
                .build();
    }

    @Test
    @DisplayName("findAll returns list of domain resources")
    void findAll_returnsMappedDomainList() {
        when(jpaRepository.findAll()).thenReturn(List.of(entityResource));

        List<WellnessResource> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sports Center");
        assertThat(result.get(0).getId()).isEqualTo(resourceId);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("findAll returns empty list when no resources exist")
    void findAll_noResources_returnsEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<WellnessResource> result = adapter.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByCategory returns filtered domain resources")
    void findByCategory_returnsFilteredList() {
        when(jpaRepository.findByAvailableTrueAndCategory(WellnessCategory.SPORTS))
                .thenReturn(List.of(entityResource));

        List<WellnessResource> result = adapter.findByCategory(WellnessCategory.SPORTS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("findByCategory returns empty when category has no resources")
    void findByCategory_noMatches_returnsEmpty() {
        when(jpaRepository.findByAvailableTrueAndCategory(WellnessCategory.CULTURE))
                .thenReturn(List.of());

        List<WellnessResource> result = adapter.findByCategory(WellnessCategory.CULTURE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById returns Optional with domain resource when found and available")
    void findById_found_returnsOptional() {
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.of(entityResource));

        Optional<WellnessResource> result = adapter.findById(resourceId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(resourceId);
        assertThat(result.get().getName()).isEqualTo("Sports Center");
    }

    @Test
    @DisplayName("findById returns empty when not found")
    void findById_notFound_returnsEmpty() {
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.empty());

        Optional<WellnessResource> result = adapter.findById(resourceId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById filters out inactive resources")
    void findById_inactive_returnsEmpty() {
        entityResource.setAvailable(false);
        when(jpaRepository.findById(resourceId.toString())).thenReturn(Optional.of(entityResource));

        Optional<WellnessResource> result = adapter.findById(resourceId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save persists and returns domain resource")
    void save_persistsAndReturnsDomain() {
        WellnessResource resource = WellnessResource.builder()
                .id(resourceId)
                .name("Sports Center")
                .description("Daily fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Wellness Complex")
                .contactInfo("555-0100")
                .schedule("Mon-Fri 6am-9pm")
                .available(true)
                .appointmentEmail("sports@university.edu")
                .psychologistName("Dr. Smith")
                .build();

        when(jpaRepository.save(any(WellnessResourceEntity.class))).thenReturn(entityResource);

        WellnessResource result = adapter.save(resource);

        assertThat(result.getId()).isEqualTo(resourceId);
        assertThat(result.getName()).isEqualTo("Sports Center");
        verify(jpaRepository).save(any(WellnessResourceEntity.class));
    }

    @Test
    @DisplayName("save assigns new UUID when resource has no id")
    void save_nullId_assignsNewUuid() {
        WellnessResource resource = WellnessResource.builder()
                .name("New Resource")
                .description("Desc")
                .category(WellnessCategory.HEALTH)
                .location("Main Campus")
                .available(true)
                .build();

        WellnessResourceEntity savedEntity = WellnessResourceEntity.builder()
                .id(UUID.randomUUID().toString())
                .name("New Resource")
                .description("Desc")
                .category(WellnessCategory.HEALTH)
                .location("Main Campus")
                .available(true)
                .build();

        when(jpaRepository.save(any(WellnessResourceEntity.class))).thenReturn(savedEntity);

        WellnessResource result = adapter.save(resource);

        assertThat(result.getId()).isNotNull();
        verify(jpaRepository).save(any(WellnessResourceEntity.class));
    }

    @Test
    @DisplayName("deleteById delegates to jpaRepository")
    void deleteById_delegatesToJpa() {
        adapter.deleteById(resourceId);

        verify(jpaRepository).deleteById(resourceId.toString());
    }

    @Test
    @DisplayName("existsById returns true when resource exists")
    void existsById_exists_returnsTrue() {
        when(jpaRepository.existsById(resourceId.toString())).thenReturn(true);

        boolean result = adapter.existsById(resourceId);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsById returns false when resource does not exist")
    void existsById_notExists_returnsFalse() {
        when(jpaRepository.existsById(resourceId.toString())).thenReturn(false);

        boolean result = adapter.existsById(resourceId);

        assertThat(result).isFalse();
    }
}
