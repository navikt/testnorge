package no.nav.testnav.apps.apptilganganalyseservice.domain;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.testnav.apps.apptilganganalyseservice.dto.AccessDTO;

public class Access {
    private final String name;
    private final List<Application> accessTo;
    private final List<Application> accessFrom;

    public Access(String name, List<Application> applications) {
        this.name = name;
        this.accessTo = applications
                .stream()
                .filter(application -> application.getInbound().contains(name))
                .collect(Collectors.toList());
        this.accessFrom = applications
                .stream()
                .filter(application -> application.getOutbound().contains(name))
                .collect(Collectors.toList());
    }

    public AccessDTO toDTO() {
        return new AccessDTO(
                name,
                accessTo.stream().map(Application::toDTO).collect(Collectors.toList()),
                accessFrom.stream().map(Application::toDTO).collect(Collectors.toList())
        );
    }
}
