package no.nav.registre.testnorge.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsDoedsmeldingDTO;
import no.nav.registre.testnorge.endringsmeldingservice.consumer.dto.TpsFoedselsmeldingDTO;

@RequiredArgsConstructor
public class SendFoedselsmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final TpsFoedselsmeldingDTO dto;

    @Override
    public void run() {
        webClient
                .post()
                .uri("/api​/v1​/tpsmelding​/foedselsmelding")
                .body(BodyInserters.fromPublisher(Mono.just(dto), TpsFoedselsmeldingDTO.class))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
