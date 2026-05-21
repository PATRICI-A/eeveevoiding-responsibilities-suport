package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.application.dto.AppointmentMailtoResponse;
import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for querying wellness resources (RF23).
 * Defines the operations students may use to browse available campus resources.
 */
public interface GetWellnessResourcesUseCase {

    /**
     * Returns all wellness resources, optionally filtered by category.
     *
     * @param category optional category filter; when null, all categories are returned
     * @return list of matching wellness resources
     */
    List<WellnessResource> getAllResources(WellnessCategory category);

    /**
     * Returns a single wellness resource by its unique identifier.
     *
     * @param id the UUID of the resource to retrieve
     * @return an Optional containing the resource, or empty if not found
     */
    Optional<WellnessResource> getResourceById(UUID id);

    /**
     * Generates a pre-built mailto for requesting a psychological appointment (RF23 HU-23-03).
     * Only valid for resources with category {@code EMOTIONAL_SUPPORT}.
     *
     * @param resourceId the UUID of the EMOTIONAL_SUPPORT resource
     * @param studentId  the student's identifier extracted from the JWT
     * @return the mailto components ready for the client to open in an email app
     */
    AppointmentMailtoResponse generateAppointmentMailto(UUID resourceId, String studentId);
}
