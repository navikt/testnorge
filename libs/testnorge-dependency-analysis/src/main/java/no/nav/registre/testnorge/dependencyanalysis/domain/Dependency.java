package no.nav.registre.testnorge.dependencyanalysis.domain;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.dependencyanalysis.v1.DependencyDTO;

public class Dependency {
    private final String name;
    private final boolean external;

    public Dependency(DependencyOn dependencyOf) {
        name = dependencyOf.value();
        external = dependencyOf.external();
    }

    public String getName() {
        return name;
    }

    public boolean isExternal() {
        return external;
    }

    public DependencyDTO toTDO(){
        return new DependencyDTO(name, external);
    }

}
