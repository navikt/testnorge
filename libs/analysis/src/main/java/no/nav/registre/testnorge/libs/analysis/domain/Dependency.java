package no.nav.registre.testnorge.libs.analysis.domain;

import no.nav.registre.testnorge.libs.analysis.DependencyOn;

public class Dependency {
    private final String name;
    private final String cluster;
    private final String namespace;

    public Dependency(DependencyOn dependencyOf) {
        name = dependencyOf.name();
        cluster = dependencyOf.cluster();
        namespace = dependencyOf.namespace();
    }

    public String getName() {
        return name;
    }

    public String getCluster() {
        return cluster;
    }

    public String getNamespace() {
        return namespace;
    }
}
