package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.exceptions.ResourceNotFoundException;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.domain.valueobjects.ResourceId;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourcePersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.WellnessResourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WellnessResourceRepositoryAdapter implements WellnessResourceRepositoryPort {

    private final WellnessResourceJpaRepository jpaRepository;
    private final WellnessResourcePersistenceMapper persistenceMapper;

    @Override
    public List<WellnessResource> findAllActive(Optional<WellnessCategory> category) {
        return category
                .map(c -> jpaRepository.findAllByCategoryAndActiveTrue(c))
                .orElseGet(() -> jpaRepository.findAllByActiveTrue())
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<WellnessResource> findById(ResourceId id) {
        return jpaRepository.findById(id.value())
                .map(persistenceMapper::toDomain);
    }

    @Override
    public WellnessResource save(WellnessResource resource) {
        return persistenceMapper.toDomain(
                jpaRepository.save(persistenceMapper.toEntity(resource))
        );
    }

    @Override
    public WellnessResource update(WellnessResource resource) {
        if (!jpaRepository.existsById(resource.getId().value())) {
            throw new ResourceNotFoundException(resource.getId().toString());
        }
        return persistenceMapper.toDomain(
                jpaRepository.save(persistenceMapper.toEntity(resource))
        );
    }

    @Override
    public void deleteById(ResourceId id) {
        if (!jpaRepository.existsById(id.value())) {
            throw new ResourceNotFoundException(id.toString());
        }
        jpaRepository.deleteById(id.value());
    }
}