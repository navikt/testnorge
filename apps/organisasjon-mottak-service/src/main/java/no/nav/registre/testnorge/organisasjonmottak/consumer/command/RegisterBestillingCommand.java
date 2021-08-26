package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.organiasjonbestilling.v2.OrderDTO;

@RequiredArgsConstructor
public class RegisterBestillingCommand implements Callable<Mono<OrderDTO>> {
    private final WebClient webClient;
    private final String token;
    private final OrderDTO dto;

    @Override
    public Mono<OrderDTO> call() {
        return webClient
                .post()
                .uri("/api/v2/order")
                .body(BodyInserters.fromPublisher(Mono.just(dto), OrderDTO.class))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrderDTO.class);
    }
}
