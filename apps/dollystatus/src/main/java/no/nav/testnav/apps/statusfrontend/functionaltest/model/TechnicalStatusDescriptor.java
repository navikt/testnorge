package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import java.util.Objects;
import java.util.Set;

public record TechnicalStatusDescriptor(
        SystemId systemId,
        DisplayName displayName,
        Set<FunctionalTestEnvironment> environments
) {

    public TechnicalStatusDescriptor {
        Objects.requireNonNull(systemId);
        Objects.requireNonNull(displayName);
        environments = Set.copyOf(environments);
        if (environments.isEmpty()) {
            throw new IllegalArgumentException("At least one environment is required.");
        }
    }
}
