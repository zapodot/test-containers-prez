package org.zapodot.testcontainers.sample.model;

public record Role(long id, String name) {
    public Role {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
    }
}
