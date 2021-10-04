package no.nav.testnav.apps.persontilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.dto.AccessDTO;

@RequiredArgsConstructor
public class GetPersonAccessCommand implements Callable<Mono<AccessDTO[]>> {
    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String serviceCode;
    private final String serviceEdition;
    private final String apiKey;

    @Override
    public Mono<AccessDTO[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/serviceowner/reportees")
                        .queryParam("subject", ident)
                        .queryParam("serviceCode", serviceCode)
                        .queryParam("serviceEdition", serviceEdition)
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .retrieve()
                .bodyToMono(AccessDTO[].class);
    }
}
