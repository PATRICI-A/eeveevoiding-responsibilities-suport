package edu.eci.patricia.infrastructure.adapters.adapter;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;
import edu.eci.patricia.domain.ports.out.WellnessResourceRepository;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.WellnessResourcePersistenceMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.JpaWellnessResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WellnessResourceRepositoryAdapter implements WellnessResourceRepository {

    private final JpaWellnessResourceRepository jpaRepository;
    private final WellnessResourcePersistenceMapper mapper;

    @Override
    public List<WellnessResource> findAllActive() {
        return jpaRepository.findAllByActiveTrue()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<WellnessResource> findActiveByCategory(WellnessCategory category) {
        return jpaRepository.findAllByActiveTrueAndCategory(category)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WellnessResource> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
