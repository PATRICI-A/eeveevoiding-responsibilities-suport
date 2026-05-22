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

@ExtendWith(MockitoExtension.class)
class WellnessResourceServiceTest {

    @Mock
    private WellnessResourceRepositoryPort repositoryPort;

    @InjectMocks
    private WellnessResourceService service;

    private WellnessResource emotionalResource;
    private WellnessResource sportsResource;
    private UUID resourceId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        emotionalResource = WellnessResource.builder()
                .id(resourceId)
                .name("Counseling Center")
                .description("Individual and group therapy")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .location("Building A, Room 101")
                .available(true)
                .appointmentEmail("psicologia@eci.edu.co")
                .psychologistName("Dra. María García")
                .build();

        sportsResource = WellnessResource.builder()
                .id(UUID.randomUUID())
                .name("Sports Center")
                .category(WellnessCategory.SPORTS)
                .location("Sports Complex")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("getAllResources with null category returns all resources")
    void getAllResources_nullCategory_returnsAll() {
        when(repositoryPort.findAll()).thenReturn(List.of(emotionalResource));

        List<WellnessResource> result = service.getAllResources(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Counseling Center");
        verify(repositoryPort).findAll();
    }

    @Test
    @DisplayName("getAllResources with specific category returns filtered resources")
    void getAllResources_withCategory_returnsFilteredList() {
        when(repositoryPort.findByCategory(WellnessCategory.EMOTIONAL_SUPPORT)).thenReturn(List.of(emotionalResource));

        List<WellnessResource> result = service.getAllResources(WellnessCategory.EMOTIONAL_SUPPORT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo(WellnessCategory.EMOTIONAL_SUPPORT);
        verify(repositoryPort).findByCategory(WellnessCategory.EMOTIONAL_SUPPORT);
    }

    @Test
    @DisplayName("getResourceById returns resource when found")
    void getResourceById_found_returnsResource() {
        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(emotionalResource));

        Optional<WellnessResource> result = service.getResourceById(resourceId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(resourceId);
    }

    @Test
    @DisplayName("getResourceById returns empty Optional when not found")
    void getResourceById_notFound_returnsEmpty() {
        UUID unknownId = UUID.randomUUID();
        when(repositoryPort.findById(unknownId)).thenReturn(Optional.empty());

        Optional<WellnessResource> result = service.getResourceById(unknownId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createResource persists and returns the resource")
    void createResource_validResource_returnsSaved() {
        when(repositoryPort.save(any(WellnessResource.class))).thenReturn(emotionalResource);

        WellnessResource created = service.createResource(emotionalResource);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(resourceId);
        verify(repositoryPort).save(emotionalResource);
    }

    @Test
    @DisplayName("updateResource throws ResourceNotFoundException when resource does not exist")
    void updateResource_nonExistentId_throwsResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(repositoryPort.existsById(nonExistentId)).thenReturn(false);

        assertThatThrownBy(() -> service.updateResource(nonExistentId, emotionalResource))
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
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
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
    @DisplayName("deleteResource succeeds when resource exists")
    void deleteResource_existingId_succeeds() {
        when(repositoryPort.existsById(resourceId)).thenReturn(true);

        service.deleteResource(resourceId);

        verify(repositoryPort).deleteById(resourceId);
    }

    @Test
    @DisplayName("generateAppointmentMailto returns mailto for EMOTIONAL_SUPPORT resource")
    void generateAppointmentMailto_emotionalSupportResource_returnsMailto() {
        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(emotionalResource));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid-123");

        assertThat(response).isNotNull();
        assertThat(response.getAppointmentEmailTo()).isEqualTo("psicologia@eci.edu.co");
        assertThat(response.getAppointmentEmailSubject()).isEqualTo("Solicitud de cita psicológica - PATRICI.A");
        assertThat(response.getAppointmentEmailBody()).contains("Dra. María García");
        assertThat(response.getAppointmentEmailBody()).contains("student-uuid-123");
    }

    @Test
    @DisplayName("generateAppointmentMailto throws WellnessException for non-EMOTIONAL_SUPPORT resource")
    void generateAppointmentMailto_nonEmotionalSupport_throwsWellnessException() {
        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(sportsResource));

        assertThatThrownBy(() -> service.generateAppointmentMailto(resourceId, "student-uuid"))
                .isInstanceOf(WellnessException.class)
                .hasMessageContaining("EMOTIONAL_SUPPORT");
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
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .location("Building C")
                .contactInfo("bienestar@eci.edu.co")
                .available(true)
                .build();

        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(resourceNoEmail));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid");

        assertThat(response.getAppointmentEmailTo()).isEqualTo("bienestar@eci.edu.co");
    }

    @Test
    @DisplayName("generateAppointmentMailto uses default email when both appointmentEmail and contactInfo are null")
    void generateAppointmentMailto_noEmail_noContactInfo_usesDefault() {
        WellnessResource resourceNoContact = WellnessResource.builder()
                .id(resourceId)
                .name("Psych Office")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .location("Building C")
                .available(true)
                .build();

        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(resourceNoContact));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid");

        assertThat(response.getAppointmentEmailTo()).isEqualTo("bienestar@eci.edu.co");
    }

    @Test
    @DisplayName("generateAppointmentMailto uses default psychologist name when psychologistName is null")
    void generateAppointmentMailto_noPsychologistName_usesDefault() {
        WellnessResource resourceNoName = WellnessResource.builder()
                .id(resourceId)
                .name("Psych Office")
                .category(WellnessCategory.EMOTIONAL_SUPPORT)
                .location("Building C")
                .appointmentEmail("psicologia@eci.edu.co")
                .available(true)
                .build();

        when(repositoryPort.findById(resourceId)).thenReturn(Optional.of(resourceNoName));

        AppointmentMailtoResponse response = service.generateAppointmentMailto(resourceId, "student-uuid");

        assertThat(response.getAppointmentEmailBody()).contains("Profesional de Bienestar");
    }
}
