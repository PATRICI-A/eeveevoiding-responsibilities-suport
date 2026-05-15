package edu.eci.patricia.application.dto.request;

import edu.eci.patricia.domain.model.enums.WellnessCategory;
import lombok.Builder;

@Builder
public record WellnessQueryRequest(WellnessCategory categoryFilter) {}