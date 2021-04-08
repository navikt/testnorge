package no.nav.registre.sdforvalter.consumer.rs.commnad;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;

@Slf4j
@RequiredArgsConstructor
public class SaveOrganisasjonFasteDataCommand implements Runnable {
    private final WebClient webClient;
    private final String token;
    private final OrganisasjonDTO dto;
    private final String gruppe;

    @Override
    public void run() {
        webClient
                .post()
                .uri("/api/v1/organisasjon")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("gruppe", gruppe)
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrganisasjonDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
