package no.nav.registre.testnorge.avhengighetsanalysefrontend.consumer.command;


import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

public class GetDependenciesCommand implements Callable<ApplicationDependenciesDTO> {
    private final String url;
    private final RestTemplate restTemplate;

    public GetDependenciesCommand(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public ApplicationDependenciesDTO call() {
        return restTemplate.getForObject(this.url, ApplicationDependenciesDTO.class);
    }
}
