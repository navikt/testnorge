package no.nav.registre.testnorge.dependencyanalysis.domain;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

public class ApplicationDependencies {
    private final String applicationName;
    private final Set<Dependency> dependencies;

    public ApplicationDependencies(String applicationName, Set<DependencyOn> dependencies) {
        this.applicationName = applicationName;
        this.dependencies = dependencies.stream().map(Dependency::new).collect(Collectors.toSet());
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public ApplicationDependenciesDTO toDTO() {
        return new ApplicationDependenciesDTO(
                applicationName,
                dependencies.stream().map(Dependency::toDTO).collect(Collectors.toSet())
        );
    }
}