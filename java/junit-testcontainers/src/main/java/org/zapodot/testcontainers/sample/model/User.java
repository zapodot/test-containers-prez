package org.zapodot.testcontainers.sample.model;

public record User(long id, String name, Iterable<Role> roles) {
    public User {
        if (name == null) {
            throw new IllegalArgumentException("Name must not be null");
        }
    }
}
