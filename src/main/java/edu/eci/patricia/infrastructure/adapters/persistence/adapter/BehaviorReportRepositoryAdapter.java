package edu.eci.patricia.infrastructure.adapters.persistence.adapter;

import edu.eci.patricia.domain.model.BehaviorReport;
import edu.eci.patricia.domain.ports.out.BehaviorReportRepositoryPort;
import edu.eci.patricia.infrastructure.adapters.persistence.mapper.BehaviorReportMapper;
import edu.eci.patricia.infrastructure.adapters.persistence.repository.BehaviorReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing {@link BehaviorReportRepositoryPort} via Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class BehaviorReportRepositoryAdapter implements BehaviorReportRepositoryPort {

    private final BehaviorReportJpaRepository jpaRepository;
    private final BehaviorReportMapper mapper;

    /** {@inheritDoc} */
    @Override
    public BehaviorReport save(BehaviorReport report) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(report)));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<BehaviorReport> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public List<BehaviorReport> findByReporterId(UUID reporterId) {
        return jpaRepository.findByReporterId(reporterId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
