package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WellnessResourceService}.
 */
@ExtendWith(MockitoExtension.class)
class WellnessResourceServiceTest {

    @Mock
    private WellnessResourceRepositoryPort repositoryPort;

    @InjectMocks
    private WellnessResourceService service;

    private WellnessResource sampleResource;
    private UUID resourceId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        sampleResource = WellnessResource.builder()
                .id(resourceId)
                .name("Counseling Center")
                .description("Individual and group therapy")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building A, Room 101")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("getAllResources with null category should return all resources")
    void getAllResources_nullCategory_returnsAll() {
        when(repositoryPort.findAll()).thenReturn(List.of(sampleResource));

        List<WellnessResource> result = service.getAllResources(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Counseling Center");
        verify(repositoryPort).findAll();
    }

    @Test
    @DisplayName("getAllResources with specific category should return filtered resources")
    void getAllResources_withCategory_returnsFilteredList() {
        when(repositoryPort.findByCategory(WellnessCategory.MENTAL_HEALTH)).thenReturn(List.of(sampleResource));

        List<WellnessResource> result = service.getAllResources(WellnessCategory.MENTAL_HEALTH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
        verify(repositoryPort).findByCategory(WellnessCategory.MENTAL_HEALTH);
    }

    @Test
    @DisplayName("createResource should persist and return the resource")
    void createResource_validResource_returnsSaved() {
        when(repositoryPort.save(any(WellnessResource.class))).thenReturn(sampleResource);

        WellnessResource created = service.createResource(sampleResource);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(resourceId);
        verify(repositoryPort).save(sampleResource);
    }

    @Test
    @DisplayName("updateResource should throw ResourceNotFoundException when resource does not exist")
    void updateResource_nonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> service.updateResource(nonExistentId, sampleResource))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    @DisplayName("updateResource should save and return the updated resource when found")
    void updateResource_existingId_returnsUpdated() {
        WellnessResource updated = WellnessResource.builder()
                .id(resourceId)
                .name("Updated Counseling Center")
                .description("Updated description")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building B")
                .available(true)
                .build();

        when(repositoryPort.existsById(resourceId)).thenReturn(true);
        when(repositoryPort.save(any(WellnessResource.class))).thenReturn(updated);

        WellnessResource result = service.updateResource(resourceId, updated);

        assertThat(result.getName()).isEqualTo("Updated Counseling Center");
        assertThat(result.getId()).isEqualTo(resourceId);
    }

    @Test
    @DisplayName("deleteResource should throw ResourceNotFoundException when resource does not exist")
    void deleteResource_nonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteResource(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getResourceById should return empty Optional when resource not found")
    void getResourceById_nonExistentId_returnsEmptyOptional() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<WellnessResource> result = service.getResourceById(nonExistentId);

        assertThat(result).isEmpty();
    }
}
