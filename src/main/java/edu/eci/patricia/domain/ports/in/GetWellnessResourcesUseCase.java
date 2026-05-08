package edu.eci.patricia.domain.ports.in;

import edu.eci.patricia.domain.model.WellnessResource;
import edu.eci.patricia.domain.model.enums.WellnessCategory;

import java.util.List;

//Para consultar los recursos de bienestar
public interface GetWellnessResourcesUseCase {
    List<WellnessResource> execute(WellnessCategory categoryFilter);
}