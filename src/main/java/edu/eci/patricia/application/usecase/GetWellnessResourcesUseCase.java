package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesPort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GetWellnessResourcesUseCase implements GetWellnessResourcesPort {

    private final WellnessResourceRepositoryPort repository;

    @Override
    public List<WellnessResource> getAll(Optional<WellnessCategory> category) {
        return repository.findAllActive(category);
    }

    @Override
    public WellnessResource getById(ResourceId id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
    }
}