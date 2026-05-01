package edu.eci.patricia.infrastructure.adapters.persistence.repository;

import edu.eci.patricia.domain.model.Event;
import edu.eci.patricia.domain.model.enums.CategoriaEvento;
import edu.eci.patricia.domain.model.enums.EstadoEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface EventRepository extends MongoRepository<Event, String> {

    // Nos da todos los eventos con su estado
    Page<Event> findAllByStatus(EstadoEvento status, Pageable pageable);

    // Da los eventos filtrados por categoria y estado
    Page<Event> findAllByStatusAndCategory(EstadoEvento status, CategoriaEvento category, Pageable pageable);

    // Nos da eventos filtrados por fecha
    Page<Event> findAllByStatusAndStartDateTimeBetween(
            EstadoEvento status,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    // Nos da los eventos filtrados por estado, categoría y rango de fecha.
    Page<Event> findAllByStatusAndCategoryAndStartDateTimeBetween(
            EstadoEvento status,
            CategoriaEvento category,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    // ara verificar si existe un evento con el ID
    boolean existsByIdAndOrganizerId(String id, String organizerId);
}