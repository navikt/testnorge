package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Callable;

import no.nav.testnav.apps.personservice.consumer.dto.tpsf.IdentMiljoeRequest;
import no.nav.testnav.apps.personservice.consumer.dto.tpsf.PersonMiljoeResponse;

@RequiredArgsConstructor
@Slf4j
public class GetTpsPersonCommand implements Callable<Mono<Optional<PersonMiljoeResponse>>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String miljoe;

    @Override
    public Mono<Optional<PersonMiljoeResponse>> call() {

        IdentMiljoeRequest requestBody = new IdentMiljoeRequest(ident, Collections.singletonList(miljoe));
        log.debug("Henter {} fra TPS miljø {}", ident, miljoe);

        return webClient
                .post()
                .uri("/api/v1/dolly/testdata/import")
                .body(BodyInserters.fromValue(requestBody))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(PersonMiljoeResponse[].class)
                .map(value -> value.length == 0 ? Optional.empty() : Optional.of(value[0]));
    }
}