package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.ports.in.DeleteWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteWellnessResourceUseCase implements DeleteWellnessResourcePort {

    private final WellnessResourceRepositoryPort repository;

    @Override
    public void delete(ResourceId id) {
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));
        repository.deleteById(id);
    }
}