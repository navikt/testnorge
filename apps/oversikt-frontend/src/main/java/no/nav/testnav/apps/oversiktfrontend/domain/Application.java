package no.nav.testnav.apps.oversiktfrontend.domain;

import lombok.Value;

import no.nav.testnav.apps.oversiktfrontend.dto.ApplicationDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Value
public class Application {
    String cluster;
    String namespace;
    String name;

    public Application(no.nav.testnav.apps.oversiktfrontend.consumer.dto.ApplicationDTO dto) {
        cluster = dto.getCluster();
        name = dto.getName();
        namespace = dto.getNamespace();
    }

    public Application(ApplicationDTO dto) {
        cluster = dto.getCluster();
        name = dto.getName();
        namespace = dto.getNamespace();
    }

    public ApplicationDTO toDTO() {
        return ApplicationDTO
                .builder()
                .cluster(cluster)
                .namespace(namespace)
                .name(name)
                .build();
    }

    public ServerProperties toServerProperties() {
        return ServerProperties
                .builder()
                .cluster(cluster)
                .namespace(namespace)
                .name(name)
                .build();
    }
}
