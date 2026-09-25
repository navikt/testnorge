package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;

public record DisplayName(String value) {

    public DisplayName {
        Objects.requireNonNull(value);
        if (value.isBlank() || value.length() > 100) {
            throw new IllegalArgumentException("Display name is invalid.");
        }
    }
}
