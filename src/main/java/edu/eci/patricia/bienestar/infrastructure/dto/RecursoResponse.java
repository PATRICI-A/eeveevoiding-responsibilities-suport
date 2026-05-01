package edu.eci.patricia.bienestar.infrastructure.dto;

import edu.eci.patricia.bienestar.domain.model.TipoRecurso;
import lombok.Data;

@Data
public class RecursoResponse {

    private String id;
    private String nombre;
    private String descripcion;
    private TipoRecurso categoria;
    private String contacto;
    private String horario;
    private String ubicacion;
    private Boolean activo;
}
