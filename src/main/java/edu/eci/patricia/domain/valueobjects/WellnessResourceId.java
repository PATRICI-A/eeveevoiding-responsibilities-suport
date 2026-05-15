package edu.eci.patricia.domain.valueobjects;

import java.util.UUID;

public record WellnessResourceId(UUID value) {

    public WellnessResourceId {
        if (value == null) {
            throw new IllegalArgumentException("WellnessResourceId cannot be null");
        }
    }

    public static WellnessResourceId generate() {
        return new WellnessResourceId(UUID.randomUUID());
    }

    public static WellnessResourceId of(UUID value) {
        return new WellnessResourceId(value);
    }

    public static WellnessResourceId of(String value) {
        return new WellnessResourceId(UUID.fromString(value));
    }
}