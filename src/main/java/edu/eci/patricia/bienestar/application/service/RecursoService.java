package edu.eci.patricia.bienestar.application.service;

import edu.eci.patricia.bienestar.domain.model.RecursoBienestar;
import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import edu.eci.patricia.bienestar.domain.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecursoService {

    private final RecursoRepository recursoRepository;

    public List<RecursoBienestar> obtenerTodos() {
        return recursoRepository.findByActivoTrueOrderByCategoria();
    }

    public List<RecursoBienestar> obtenerPorCategoria(TipoRecurso categoria) {
        return recursoRepository.findByActivoTrueAndCategoria(categoria);
    }
}
