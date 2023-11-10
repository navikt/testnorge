package no.nav.testnav.apps.hodejegeren.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTpsIdenterCommand implements Callable<Mono<Set<String>>> {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;
    private final String token;
    private final String aarsakkode;
    private final String transaksjonstype;
    private final Long avspillergruppeId;

    @Override
    public Mono<Set<String>> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/endringsmelding/skd/identer/{avspillergruppeId}")
                        .queryParam("aarsakskode", aarsakkode)
                        .queryParam("transaksjonstype", transaksjonstype)
                        .build(avspillergruppeId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }
}
