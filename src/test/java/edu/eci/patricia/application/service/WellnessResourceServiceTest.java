package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.AppointmentMailtoResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
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
    private WellnessResource mentalHealthResource;
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
                .appointmentEmail("psicologia@eci.edu.co")
                .psychologistName("Dra. María García")
                .build();

        mentalHealthResource = sampleResource;
    }

    @Test
    @DisplayName("getAllResources with null category returns all resources")
    void getAllResources_nullCategory_returnsAll() {
        when(repositoryPort.findAll()).thenReturn(List.of(sampleResource));

        List<WellnessResource> result = service.getAllResources(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Counseling Center");
        verify(repositoryPort).findAll();
    }

    @Test
    @DisplayName("getAllResources with specific category returns filtered resources")
    void getAllResources_withCategory_returnsFilteredList() {
        when(repositoryPort.findByCategory(WellnessCategory.MENTAL_HEALTH)).thenReturn(List.of(sampleResource));

        List<WellnessResource> result = service.getAllResources(WellnessCategory.MENTAL_HEALTH);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.MENTAL_HEALTH);
        verify(repositoryPort).findByCategory(WellnessCategory.MENTAL_HEALTH);
    }

    @Test
    @DisplayName("createResource persists and returns the resource")
    void createResource_validResource_returnsSaved() {
        when(repositoryPort.save(any(WellnessResource.class))).thenReturn(sampleResource);

        WellnessResource created = service.createResource(sampleResource);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(resourceId);
        verify(repositoryPort).save(sampleResource);
    }

    @Test
    @DisplayName("updateResource throws ResourceNotFoundException when resource does not exist")
    void updateResource_nonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> service.updateResource(nonExistentId, sampleResource))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    @DisplayName("updateResource saves and returns updated resource when found")
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
    @DisplayName("deleteResource throws ResourceNotFoundException when resource does not exist")
    void deleteResource_nonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteResource(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getResourceById returns empty Optional when resource not found")
    void getResourceById_nonExistentId_returnsEmptyOptional() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<WellnessResource> result = service.getResourceById(nonExistentId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("generateAppointmentMailto returns mailto for MENTAL_HEALTH resource")
    void generateAppointmentMailto_mentalHealthResource_returnsMailto() {
        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(mentalHealthResource));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid-123");

        assertThat(response).isNotNull();
        assertThat(response.getAppointmentEmailTo()).isEqualTo("psicologia@eci.edu.co");
        assertThat(response.getAppointmentEmailSubject()).isEqualTo("Solicitud de cita psicológica - PATRICI.A");
        assertThat(response.getAppointmentEmailBody()).contains("Dra. María García");
        assertThat(response.getAppointmentEmailBody()).contains("student-uuid-123");
    }

    @Test
    @DisplayName("generateAppointmentMailto throws WellnessException for non-MENTAL_HEALTH resource")
    void generateAppointmentMailto_nonMentalHealthResource_throwsWellnessException() {
        WellnessResource sportsResource = WellnessResource.builder()
                .id(resourceId)
                .name("Sports Center")
                .category(WellnessCategory.SPORTS)
                .location("Sports Complex")
                .available(true)
                .build();

        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(sportsResource));

        assertThatThrownBy(() -> service.generateAppointmentMailto(resourceId, "student-uuid"))
                .isInstanceOf(WellnessException.class)
                .hasMessageContaining("MENTAL_HEALTH");
    }

    @Test
    @DisplayName("generateAppointmentMailto throws ResourceNotFoundException when resource not found")
    void generateAppointmentMailto_resourceNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(repositoryPort.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateAppointmentMailto(unknownId, "student-uuid"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("generateAppointmentMailto uses contactInfo as fallback when appointmentEmail is null")
    void generateAppointmentMailto_noAppointmentEmail_usesContactInfoFallback() {
        WellnessResource resourceNoEmail = WellnessResource.builder()
                .id(resourceId)
                .name("Psych Office")
                .category(WellnessCategory.MENTAL_HEALTH)
                .location("Building C")
                .contactInfo("bienestar@eci.edu.co")
                .available(true)
                .build();

        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(resourceNoEmail));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid");

        assertThat(response.getAppointmentEmailTo()).isEqualTo("bienestar@eci.edu.co");
    }
}
