package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Input port for querying wellness resources.
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
}
