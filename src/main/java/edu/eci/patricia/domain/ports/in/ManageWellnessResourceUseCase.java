package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;

import java.util.UUID;

/**
 * Input port for creating, updating, and deleting wellness resources.
 * These operations are typically restricted to admin users.
 */
public interface ManageWellnessResourceUseCase {

    /**
     * Creates a new wellness resource in the system.
     *
     * @param resource the resource to persist (id may be null — will be assigned)
     * @return the persisted resource with its generated id and createdAt timestamp
     */
    WellnessResource createResource(WellnessResource resource);

    /**
     * Updates an existing wellness resource.
     *
     * @param id       the UUID of the resource to update
     * @param resource the new data to apply
     * @return the updated resource
     */
    WellnessResource updateResource(UUID id, WellnessResource resource);

    /**
     * Deletes a wellness resource by its identifier.
     *
     * @param id the UUID of the resource to delete
     */
    void deleteResource(UUID id);
}
