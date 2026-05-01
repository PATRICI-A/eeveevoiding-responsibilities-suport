package edu.eci.patricia.bienestar.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "wellness_resources")
public class RecursoBienestar {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private TipoRecurso categoria;
    private String contacto;
    private String horario;
    private String ubicacion;
    private Boolean activo = true;
}
