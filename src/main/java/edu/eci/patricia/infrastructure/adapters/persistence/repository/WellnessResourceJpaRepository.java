package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.WellnessCategory;
import edu.eci.patricia.infrastructure.adapters.persistence.entity.WellnessResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link WellnessResourceEntity}.
 * Provides CRUD operations and category-based queries.
 */
@Repository
public interface WellnessResourceJpaRepository extends JpaRepository<WellnessResourceEntity, UUID> {

    /**
     * Retrieves all wellness resources belonging to the given category.
     *
     * @param category the category to filter by
     * @return list of matching wellness resource entities
     */
    List<WellnessResourceEntity> findByCategory(WellnessCategory category);
}
