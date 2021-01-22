package no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.command;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;

@RequiredArgsConstructor
public class SaveAnalyseCommand implements Runnable {

    private final WebClient webClient;
    private final String token;
    private final ApplicationInfoDTO dto;

    @Override
    public void run() {
        webClient
                .put()
                .uri("/api/v1/applications")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(dto), ApplicationInfoDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
