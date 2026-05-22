package edu.eci.patricia.application.service;

import edu.eci.patricia.application.dto.AppointmentMailtoResponse;
import edu.eci.patricia.domain.exception.ResourceNotFoundException;
import edu.eci.patricia.domain.exception.WellnessException;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import edu.eci.patricia.domain.ports.in.ManageWellnessResourceUseCase;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service implementing wellness resource query and management use cases (RF23).
 */
@Service
@RequiredArgsConstructor
public class WellnessResourceService implements GetWellnessResourcesUseCase, ManageWellnessResourceUseCase {

    private final WellnessResourceRepositoryPort repositoryPort;

    /** {@inheritDoc} */
    @Override
    public List<WellnessResource> getAllResources(WellnessCategory category) {
        if (category == null) {
            return repositoryPort.findAll();
        }
        return repositoryPort.findByCategory(category);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<WellnessResource> getResourceById(UUID id) {
        return repositoryPort.findById(id);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no resource exists with the given id
     * @throws WellnessException         if the resource category is not EMOTIONAL_SUPPORT
     */
    @Override
    public AppointmentMailtoResponse generateAppointmentMailto(UUID resourceId, String studentId) {
        WellnessResource resource = repositoryPort.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Wellness resource not found with id: " + resourceId));

        if (resource.getCategory() != WellnessCategory.EMOTIONAL_SUPPORT) {
            throw new WellnessException(
                    "Appointment mailto is only available for EMOTIONAL_SUPPORT resources");
        }

        String psychologistName = resource.getPsychologistName() != null
                ? resource.getPsychologistName() : "Profesional de Bienestar";
        String emailTo = resource.getAppointmentEmail() != null
                ? resource.getAppointmentEmail()
                : (resource.getContactInfo() != null ? resource.getContactInfo() : "bienestar@eci.edu.co");

        String subject = "Solicitud de cita psicológica - PATRICI.A";
        String body = String.format(
                "Estimado/a %s,%n%n" +
                "Por medio del presente correo, solicito una cita de apoyo psicológico " +
                "a través de la plataforma PATRICI.A.%n%n" +
                "Identificación del estudiante: %s%n%n" +
                "Quedo pendiente de su respuesta.%n%n" +
                "Atentamente,%n" +
                "Estudiante ECI",
                psychologistName, studentId
        );

        return AppointmentMailtoResponse.builder()
                .appointmentEmailTo(emailTo)
                .appointmentEmailSubject(subject)
                .appointmentEmailBody(body)
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public WellnessResource createResource(WellnessResource resource) {
        return repositoryPort.save(resource);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no resource exists with the given id
     */
    @Override
    public WellnessResource updateResource(UUID id, WellnessResource resource) {
        if (!repositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Wellness resource not found with id: " + id);
        }
        resource.setId(id);
        return repositoryPort.save(resource);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if no resource exists with the given id
     */
    @Override
    public void deleteResource(UUID id) {
        if (!repositoryPort.existsById(id)) {
            throw new ResourceNotFoundException("Wellness resource not found with id: " + id);
        }
        repositoryPort.deleteById(id);
    }
}
