package edu.eci.patricia.infrastructure.config;

import edu.eci.patricia.application.usecase.*;
import edu.eci.patricia.domain.ports.in.*;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WellnessBeanConfig {

    @Bean
    public GetWellnessResourcesPort getWellnessResourcesPort(WellnessResourceRepositoryPort repository) {
        return new GetWellnessResourcesUseCase(repository);
    }

    @Bean
    public CreateWellnessResourcePort createWellnessResourcePort(WellnessResourceRepositoryPort repository) {
        return new CreateWellnessResourceUseCase(repository);
    }

    @Bean
    public UpdateWellnessResourcePort updateWellnessResourcePort(WellnessResourceRepositoryPort repository) {
        return new UpdateWellnessResourceUseCase(repository);
    }

    @Bean
    public DeleteWellnessResourcePort deleteWellnessResourcePort(WellnessResourceRepositoryPort repository) {
        return new DeleteWellnessResourceUseCase(repository);
    }

    @Bean
    public GenerateMailtoPort generateMailtoPort(WellnessResourceRepositoryPort repository) {
        return new GenerateMailtoUseCase(repository);
    }
}