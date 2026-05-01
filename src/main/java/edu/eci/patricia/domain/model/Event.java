package edu.eci.patricia.domain.model;

import edu.eci.patricia.domain.model.enums.CategoriaEvento;
import edu.eci.patricia.domain.model.enums.EstadoEvento;
import edu.eci.patricia.domain.model.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "eventos")
public class Event {

    // Identificador único generado por MongoDB
    @Id
    private String id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private String location;
    private CategoriaEvento category;
    private TipoEvento type;
    private Integer maxCapacity;
    private Integer availableCapacity;
    private String organizerId;
    @Builder.Default//El estado por defecto es activo
    private EstadoEvento status = EstadoEvento.ACTIVO;
    private LocalDateTime createdAt;

    public boolean hasAvailability() {
        if (type == TipoEvento.ABIERTO) return true;
        return availableCapacity != null && availableCapacity > 0;
    }


    public boolean isActive() {
        return EstadoEvento.ACTIVO.equals(status);
    }

    public boolean canAcceptRsvp() {
        return isActive() && hasAvailability();
    }
}