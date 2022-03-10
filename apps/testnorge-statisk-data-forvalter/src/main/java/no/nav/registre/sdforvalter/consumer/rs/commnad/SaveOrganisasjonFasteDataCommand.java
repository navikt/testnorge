package no.nav.registre.sdforvalter.consumer.rs.commnad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class SaveOrganisasjonFasteDataCommand implements Runnable {
    private final WebClient webClient;
    private final String token;
    private final OrganisasjonDTO dto;
    private final Gruppe gruppe;

    @Override
    public void run() {
        webClient
                .put()
                .uri("/api/v1/organisasjon")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("gruppe", gruppe.name())
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrganisasjonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
