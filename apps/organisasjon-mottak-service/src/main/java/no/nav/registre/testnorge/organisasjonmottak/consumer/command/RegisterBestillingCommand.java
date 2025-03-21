package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisajonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

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
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
