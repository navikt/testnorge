package no.nav.testnav.apps.apptilganganalyseservice.domain;

import lombok.Value;

import no.nav.testnav.apps.apptilganganalyseservice.dto.ApplicationDTO;

@Value
public class Application {

    public Application(ApplicationConfig applicationConfig, DeployConfig deployConfig) {
        this.name = applicationConfig.getName();
        this.cluster = deployConfig == null ? "unknown" : deployConfig.getCluster();
        this.namespace = applicationConfig.getNamespace();
    }

    String name;
    String cluster;
    String namespace;

    public ApplicationDTO toDTO() {
        return ApplicationDTO
                .builder()
                .name(name)
                .cluster(cluster)
                .namespace(namespace)
                .build();
    }
}
