package no.nav.testnav.libs.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class SaveOrganisasjonCommand implements Runnable {
    private final WebClient webClient;
    private final OrganisasjonDTO dto;
    private final String accessToken;
    private final String miljo;

    @Override
    public void run() {
        log.info("Oppretter/oppdaterer organisasjon {}", dto.getOrgnummer());
        webClient
                .put()
                .uri("/api/v1/organisasjoner")
                .header("miljo", miljo)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrganisasjonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
