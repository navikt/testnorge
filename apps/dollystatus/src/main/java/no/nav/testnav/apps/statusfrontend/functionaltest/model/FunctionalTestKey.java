package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;

public record FunctionalTestKey(SystemId systemId, FunctionalTestEnvironment environment) {

    public FunctionalTestKey {
        Objects.requireNonNull(systemId);
        Objects.requireNonNull(environment);
    }
}
