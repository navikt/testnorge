package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.OrderDTO;

@RequiredArgsConstructor
public class SaveOrganisasjonBestillingCommand implements Runnable {
    private final WebClient webClient;
    private final OrderDTO orderDTO;
    private final String token;
    private final String uuid;

    @Override
    public void run() {
        webClient
                .post()
                .uri(builder -> builder.path("/api/v1/order/{uuid}").build(uuid))
                .body(BodyInserters.fromPublisher(Mono.just(orderDTO), OrderDTO.class))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
