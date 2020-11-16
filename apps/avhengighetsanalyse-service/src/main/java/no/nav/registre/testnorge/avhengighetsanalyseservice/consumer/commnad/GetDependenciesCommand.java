package no.nav.registre.testnorge.avhengighetsanalyseservice.consumer.commnad;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.dependencyanalysis.v1.ApplicationDependenciesDTO;

public class GetDependenciesCommand implements Callable<ApplicationDependenciesDTO> {
    private final WebClient webClient;

    public GetDependenciesCommand(String host) {
        this.webClient = WebClient.builder().baseUrl(host).build();
    }

    @Override
    public ApplicationDependenciesDTO call() {
        return webClient
                .get()
                .uri("/dependencies")
                .retrieve()
                .bodyToMono(ApplicationDependenciesDTO.class)
                .block();
    }
}
