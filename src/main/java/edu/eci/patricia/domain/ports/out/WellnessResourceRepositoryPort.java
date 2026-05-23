package edu.eci.patricia.domain.ports.out;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port defining persistence operations for wellness resources.
 * Implementations reside in the infrastructure layer.
 */
public interface WellnessResourceRepositoryPort {

    /**
     * Retrieves all available wellness resources.
     *
     * @return list of all resources
     */
    List<WellnessResource> findAll();

    /**
     * Retrieves all wellness resources belonging to a specific category.
     *
     * @param category the category to filter by
     * @return list of resources in the given category
     */
    List<WellnessResource> findByCategory(WellnessCategory category);

    /**
     * Retrieves a wellness resource by its unique identifier.
     *
     * @param id the UUID of the resource
     * @return an Optional containing the resource, or empty if not found
     */
    Optional<WellnessResource> findById(UUID id);

    /**
     * Persists a new or updated wellness resource.
     *
     * @param resource the resource to save
     * @return the saved resource
     */
    WellnessResource save(WellnessResource resource);

    /**
     * Deletes a wellness resource by its identifier.
     *
     * @param id the UUID of the resource to delete
     */
    void deleteById(UUID id);

    /**
     * Checks whether a wellness resource with the given id exists.
     *
     * @param id the UUID to check
     * @return true if a resource with that id exists, false otherwise
     */
    boolean existsById(UUID id);
}
