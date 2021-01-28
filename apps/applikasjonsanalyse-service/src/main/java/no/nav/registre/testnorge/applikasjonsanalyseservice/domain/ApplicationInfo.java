package no.nav.registre.testnorge.applikasjonsanalyseservice.domain;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationDependencyModel;
import no.nav.registre.testnorge.applikasjonsanalyseservice.repository.model.ApplicationInfoModel;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;
import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

@Getter
public class ApplicationInfo {
    private final String name;
    private final String cluster;
    private final String namespace;
    private final List<Dependency> dependencies;


    public ApplicationInfo(ApplicationInfoModel model, List<ApplicationDependencyModel> dependencyModels){
        this.name = model.getName();
        this.cluster = model.getCluster();
        this.namespace = model.getNamespace();
        this.dependencies = dependencyModels.stream().map(Dependency::new).collect(Collectors.toList());
    }

    public ApplicationInfo(ApplicationInfoDTO dto) {
        this.name = dto.getName();
        this.cluster = dto.getCluster();
        this.namespace = dto.getNamespace();
        this.dependencies = dto.getDependencies().stream().map(Dependency::new).collect(Collectors.toList());
    }

    public ApplicationDependenciesDTO toDTO(){
        return ApplicationDependenciesDTO
                .builder()
                .applicationName(String.format("%s.%s.%s", cluster, namespace, name))
                .dependencies(dependencies.stream().map(Dependency::toDTO).collect(Collectors.toSet()))
                .build();
    }

    public ApplicationInfoModel toModel(){
        return ApplicationInfoModel
                .builder()
                .name(name)
                .cluster(cluster)
                .namespace(namespace)
                .build();
    }
}
