package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organiasjonbestilling.v2.OrderDTO;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(OrderDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
