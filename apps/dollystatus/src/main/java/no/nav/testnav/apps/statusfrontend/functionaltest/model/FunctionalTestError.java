package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;

public record FunctionalTestError(FunctionalTestErrorCategory category, String message) {

    public FunctionalTestError {
        Objects.requireNonNull(category);
        Objects.requireNonNull(message);
    }
}
