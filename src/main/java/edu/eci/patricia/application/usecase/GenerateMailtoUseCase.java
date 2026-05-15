package edu.eci.patricia.application.usecase;

import edu.eci.patricia.domain.exceptions.InvalidCategoryException;
import edu.eci.patricia.domain.exceptions.MailtoGenerationException;
import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.in.GenerateMailtoPort;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.domain.valueobjects.MailtoAppointment;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenerateMailtoUseCase implements GenerateMailtoPort {

    private final WellnessResourceRepositoryPort repository;

    @Override
    public MailtoAppointment generate(ResourceId id, String studentName) {
        WellnessResource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id.toString()));

        if (!resource.isMentalHealth()) {
            throw new InvalidCategoryException(resource.getCategory().name());
        }

        try {
            return resource.buildMailtoAppointment(studentName);
        } catch (Exception e) {
            throw new MailtoGenerationException(id.toString(), e);
        }
    }
}