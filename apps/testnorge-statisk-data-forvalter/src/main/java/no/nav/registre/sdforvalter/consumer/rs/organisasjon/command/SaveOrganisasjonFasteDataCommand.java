package no.nav.registre.sdforvalter.consumer.rs.organisasjon.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .headers(WebClientHeader.bearer(token))
                .header("gruppe", gruppe.name())
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrganisasjonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
