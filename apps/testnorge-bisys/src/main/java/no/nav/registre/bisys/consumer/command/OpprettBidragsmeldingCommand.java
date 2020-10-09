package no.nav.registre.bisys.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@Slf4j
@DependencyOn("bisys")
@RequiredArgsConstructor
public class OpprettBidragsmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final SyntetisertBidragsmelding bidragsmelding;

    @Override
    public void run() {
        webClient
                .post()
                .uri("/api/v1/dummy")
                .body(BodyInserters.fromPublisher(Mono.just(bidragsmelding), SyntetisertBidragsmelding.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
