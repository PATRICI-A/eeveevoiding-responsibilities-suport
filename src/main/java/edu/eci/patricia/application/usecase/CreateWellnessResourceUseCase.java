package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.in.CreateWellnessResourcePort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateWellnessResourceUseCase implements CreateWellnessResourcePort {

    private final WellnessResourceRepositoryPort repository;

    @Override
    public WellnessResource create(WellnessResource resource) {
        if (WellnessCategory.MENTAL_HEALTH.equals(resource.getCategory())) {
            return repository.save(WellnessResource.createMentalHealth(
                    resource.getName(),
                    resource.getDescription(),
                    resource.getContact(),
                    resource.getSchedule(),
                    resource.getPsychologistName(),
                    resource.getAppointmentEmail()
            ));
        }
        return repository.save(WellnessResource.createGeneral(
                resource.getName(),
                resource.getDescription(),
                resource.getContact(),
                resource.getSchedule(),
                resource.getCategory()
        ));
    }
}