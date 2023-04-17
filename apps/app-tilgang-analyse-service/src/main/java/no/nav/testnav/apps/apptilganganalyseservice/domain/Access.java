package no.nav.testnav.apps.apptilganganalyseservice.domain;

import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.apptilganganalyseservice.dto.AccessDTO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Access {
    private final String name;
    private final List<Application> accessTo;
    private final List<Application> accessFrom;

    public Access(String name, List<ApplicationConfig> applications, List<DeployConfig> deploys) {
        this.name = name;
        this.accessTo = applications
                .stream()
                .filter(application -> application.getInbound().contains(name))
                .map(application -> new Application(application, find(application, deploys)))
                .collect(Collectors.toList());
        this.accessFrom = applications
                .stream()
                .filter(application -> application.getOutbound().contains(name))
                .map(application -> new Application(application, find(application, deploys)))
                .collect(Collectors.toList());
    }

    public AccessDTO toDTO() {
        return new AccessDTO(
                name,
                accessTo.stream().map(Application::toDTO).collect(Collectors.toList()),
                accessFrom.stream().map(Application::toDTO).collect(Collectors.toList())
        );
    }

    private DeployConfig find(ApplicationConfig config, List<DeployConfig> deploys) {
        log.info("Deploys: \n{}", Json.pretty(deploys));
        return deploys
                .stream()
                .filter(value -> value.isDeploying(config))
                .findFirst()
                .orElse(null);
    }
}
