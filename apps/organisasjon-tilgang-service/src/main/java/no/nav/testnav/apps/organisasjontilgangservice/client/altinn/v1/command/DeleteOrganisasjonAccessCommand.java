package no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class DeleteOrganisasjonAccessCommand implements Callable<Mono<Void>> {
    private final WebClient webClient;
    private final String token;
    private final String apiKey;
    private final Integer id;


    @Override
    public Mono<Void> call() {
        return webClient
                .delete()
                .uri(builder -> builder.path("/api/serviceowner/Srr/{id}")
                        .build(id)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("ApiKey", apiKey)
                .header(HttpHeaders.CONTENT_TYPE, "application/hal+json")
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(value -> log.info("Organiasjon tilgang {} slettet.", id));
    }

}
