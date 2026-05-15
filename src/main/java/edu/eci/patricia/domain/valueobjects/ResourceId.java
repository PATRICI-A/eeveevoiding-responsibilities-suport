package edu.eci.patricia.domain.valueobjects;

import java.util.UUID;

public record ResourceId(UUID value) {

    public ResourceId {
        if (value == null) throw new IllegalArgumentException("ResourceId value must not be null");
    }

    public static ResourceId of(UUID value) {
        return new ResourceId(value);
    }

    public static ResourceId of(String value) {
        return new ResourceId(UUID.fromString(value));
    }

    public static ResourceId generate() {
        return new ResourceId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}