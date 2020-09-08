package no.nav.registre.testnorge.libs.dependencyanalysis.domain;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyType;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.DependencyDTO;

public class Dependency {
    private final String name;
    private final boolean external;
    private final DependencyType type;

    public Dependency(DependencyOn dependencyOf) {
        name = dependencyOf.value();
        external = dependencyOf.external();
        type = dependencyOf.type();
    }

    public String getName() {
        return name;
    }

    public boolean isExternal() {
        return external;
    }

    public DependencyType getType() {
        return type;
    }

    public DependencyDTO toDTO() {
        return new DependencyDTO(name, external);
    }

}
