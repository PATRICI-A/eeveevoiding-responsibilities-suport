package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.WellnessResourceJpaRepository;
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

/**
 * Unit tests for {@link WellnessResourceRepositoryAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class WellnessResourceRepositoryAdapterTest {

    @Mock
    private WellnessResourceJpaRepository jpaRepository;

    @Mock
    private WellnessResourceMapper mapper;

    @InjectMocks
    private WellnessResourceRepositoryAdapter adapter;

    private UUID resourceId;
    private WellnessResource domainResource;
    private WellnessResourceEntity entityResource;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();

        domainResource = WellnessResource.builder()
                .id(resourceId)
                .name("Sports Center")
                .description("Daily fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Wellness Complex")
                .available(true)
                .build();

        entityResource = WellnessResourceEntity.builder()
                .id(resourceId)
                .name("Sports Center")
                .description("Daily fitness classes")
                .category(WellnessCategory.SPORTS)
                .location("Wellness Complex")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("findAll returns list of domain resources")
    void findAll_returnsMappedDomainList() {
        when(jpaRepository.findAll()).thenReturn(List.of(entityResource));
        when(mapper.toDomain(entityResource)).thenReturn(domainResource);

        List<WellnessResource> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Sports Center");
    }

    @Test
    @DisplayName("findByCategory returns filtered domain resources")
    void findByCategory_returnsFilteredList() {
        when(jpaRepository.findByCategory(WellnessCategory.SPORTS))
                .thenReturn(List.of(entityResource));
        when(mapper.toDomain(entityResource)).thenReturn(domainResource);

        List<WellnessResource> result = adapter.findByCategory(WellnessCategory.SPORTS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.SPORTS);
    }

    @Test
    @DisplayName("findById returns Optional with domain resource when found")
    void findById_found_returnsOptional() {
        when(jpaRepository.findById(resourceId)).thenReturn(Optional.of(entityResource));
        when(mapper.toDomain(entityResource)).thenReturn(domainResource);

        Optional<WellnessResource> result = adapter.findById(resourceId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(resourceId);
    }

    @Test
    @DisplayName("findById returns empty Optional when not found")
    void findById_notFound_returnsEmpty() {
        when(jpaRepository.findById(resourceId)).thenReturn(Optional.empty());

        Optional<WellnessResource> result = adapter.findById(resourceId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("save persists entity and returns mapped domain")
    void save_persistsAndReturnsDomain() {
        when(mapper.toEntity(domainResource)).thenReturn(entityResource);
        when(jpaRepository.save(entityResource)).thenReturn(entityResource);
        when(mapper.toDomain(entityResource)).thenReturn(domainResource);

        WellnessResource saved = adapter.save(domainResource);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(resourceId);
        verify(jpaRepository).save(entityResource);
    }

    @Test
    @DisplayName("deleteById delegates to JPA repository")
    void deleteById_delegatesToJpaRepository() {
        adapter.deleteById(resourceId);

        verify(jpaRepository).deleteById(resourceId);
    }

    @Test
    @DisplayName("existsById returns true when resource exists")
    void existsById_exists_returnsTrue() {
        when(jpaRepository.existsById(resourceId)).thenReturn(true);

        assertThat(adapter.existsById(resourceId)).isTrue();
    }

    @Test
    @DisplayName("existsById returns false when resource does not exist")
    void existsById_notExists_returnsFalse() {
        when(jpaRepository.existsById(resourceId)).thenReturn(false);

        assertThat(adapter.existsById(resourceId)).isFalse();
    }
}
