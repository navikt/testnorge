package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;
import java.util.UUID;

public record RunId(UUID value) {

    public RunId {
        Objects.requireNonNull(value);
    }

    public static RunId random() {
        return new RunId(UUID.randomUUID());
    }

    public static RunId from(String value) {
        return new RunId(UUID.fromString(value));
    }
}
