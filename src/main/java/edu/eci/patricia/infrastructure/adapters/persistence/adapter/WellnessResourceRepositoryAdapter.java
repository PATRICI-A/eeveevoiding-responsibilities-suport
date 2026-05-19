package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.WellnessResourceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing {@link WellnessResourceRepositoryPort} via Spring Data JPA.
 * Translates between domain models and JPA entities using the mapper.
 */
@Component
@RequiredArgsConstructor
public class WellnessResourceRepositoryAdapter implements WellnessResourceRepositoryPort {

    private final WellnessResourceJpaRepository jpaRepository;
    private final WellnessResourceMapper mapper;

    /** {@inheritDoc} */
    @Override
    public List<WellnessResource> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public List<WellnessResource> findByCategory(WellnessCategory category) {
        return jpaRepository.findByCategory(category).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<WellnessResource> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public WellnessResource save(WellnessResource resource) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(resource)));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    /** {@inheritDoc} */
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
