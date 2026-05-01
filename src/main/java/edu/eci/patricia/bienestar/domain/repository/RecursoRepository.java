package edu.eci.patricia.bienestar.domain.repository;

import edu.eci.patricia.bienestar.domain.model.RecursoBienestar;
import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RecursoRepository extends MongoRepository<RecursoBienestar, String> {

    List<RecursoBienestar> findByActivoTrueOrderByCategoria();

    List<RecursoBienestar> findByActivoTrueAndCategoria(TipoRecurso categoria);
}
