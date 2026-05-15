package edu.eci.patricia.infrastructure.adapters.adapter;


import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.out.WellnessResourcePort;
import edu.eci.patricia.domain.valueobjects.WellnessResourceId;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourcePersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.WellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WellnessResourceAdapter implements WellnessResourcePort {

    private final WellnessResourceRepository repository;
    private final WellnessResourcePersistenceMapper mapper;

    @Override
    public List<WellnessResource> findAllActive() {
        return repository.findAllByActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<WellnessResource> findActiveByCategory(WellnessCategory category) {
        return repository.findAllByActiveTrueAndCategory(category)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<WellnessResource> findById(WellnessResourceId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public WellnessResource save(WellnessResource resource) {
        WellnessResourceEntity entity = mapper.toEntity(resource);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void deactivate(WellnessResourceId id) {
        repository.findById(id.value()).ifPresent(entity -> {
            entity.setActive(false);
            repository.save(entity);
        });
    }
}