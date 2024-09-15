package org.zapodot.testcontainers.sample.csv;

import java.util.List;

public record UserWithRoles(String name, List<String> roles) {
}
