package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
@DependencyOn("organisasjon-api")
public class SaveOrganisasjonCommand implements Runnable {
    private final WebClient webClient;
    private final OrganisasjonDTO dto;
    private final String accessToken;
    private final String miljoe;

    @Override
    public void run() {
        log.info("Oppretter/oppdaterer organisasjon {}", dto.getOrgnummer());
        webClient
                .put()
                .uri("/api/v1/organisasjoner")
                .header("miljoe", miljoe)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrganisasjonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
