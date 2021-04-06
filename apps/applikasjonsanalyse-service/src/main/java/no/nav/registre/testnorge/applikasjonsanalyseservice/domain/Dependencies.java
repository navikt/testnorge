package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

public class Dependencies {
    private final Set<ApplicationDependenciesDTO> items;

    public Dependencies(Set<ApplicationDependenciesDTO> items) {
        this.items = items;
    }

    public Dependencies() {
        this.items = new HashSet<>();
    }

    public void addAll(Dependencies other) {
        other.items.forEach(item -> {
            var current = find(items, item);
            if (current.isPresent()) {
                current.get().getDependencies().addAll(item.getDependencies());
            } else {
                items.add(item);
            }
        });
    }

    private static Optional<ApplicationDependenciesDTO> find(Set<ApplicationDependenciesDTO> items, ApplicationDependenciesDTO item) {
        return items.stream().filter(value -> value.hashCode() == item.hashCode()).findFirst();
    }

    public Set<ApplicationDependenciesDTO> toDTO(){
        return items;
    }
}
