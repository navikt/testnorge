package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;


import lombok.Getter;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.DependencyDTO;

@Getter
public class Dependency {
    private final String name;
    private final String cluster;
    private final String namespace;

    public Dependency(DependencyDTO dto) {
        this.name = dto.getName();
        this.cluster = dto.getCluster();
        this.namespace = dto.getNamespace();
    }

    public Dependency(ApplicationDependencyModel model) {
        this.name = model.getName();
        this.cluster = model.getCluster();
        this.namespace = model.getNamespace();
    }

    public no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.DependencyDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.DependencyDTO
                .builder()
                .external(false)
                .name(String.format("%s.%s.%s", cluster, namespace, name))
                .build();
    }

    public ApplicationDependencyModel toModel(){
        return ApplicationDependencyModel
                .builder()
                .name(name)
                .cluster(cluster)
                .namespace(namespace)
                .build();
    }

}
