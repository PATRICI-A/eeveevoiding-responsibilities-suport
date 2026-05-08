package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.GetWellnessResourcesUseCase;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetWellnessResourcesUseCaseImpl implements GetWellnessResourcesUseCase {

    private final WellnessResourceRepository repository;

    @Override
    public List<WellnessResource> execute(WellnessCategory categoryFilter) {
        if (categoryFilter == null) {
            return repository.findAllActive();
        }
        return repository.findActiveByCategory(categoryFilter);
    }
}