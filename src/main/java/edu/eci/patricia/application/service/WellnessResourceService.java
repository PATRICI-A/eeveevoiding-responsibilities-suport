package edu.eci.patricia.application.service;

import edu.eci.patricia.domain.exception.ResourceNotFoundException;
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
 * Application service implementing wellness resource query and management use cases.
 * Orchestrates domain logic and delegates persistence to the repository port.
 */
@Service
@RequiredArgsConstructor
public class WellnessResourceService implements GetWellnessResourcesUseCase, ManageWellnessResourceUseCase {

    private final WellnessResourceRepositoryPort repositoryPort;

    /**
     * {@inheritDoc}
     * When {@code category} is null all resources are returned; otherwise results are filtered.
     */
    @Override
    public List<WellnessResource> getAllResources(WellnessCategory category) {
        if (category == null) {
            return repositoryPort.findAll();
        }
        return repositoryPort.findByCategory(category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<WellnessResource> getResourceById(UUID id) {
        return repositoryPort.findById(id);
    }

    /**
     * {@inheritDoc}
     */
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
