package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateOrganisasjonBestillingCommand implements Runnable {

    private final WebClient webClient;
    private final OrderDTO orderDTO;
    private final String token;
    private final String uuid;
    private final Long id;

    @Override
    public void run() {
        webClient
                .put()
                .uri(builder -> builder.path("/api/v1/order/{uuid}/items/{id}").build(uuid, id))
                .body(BodyInserters.fromPublisher(Mono.just(orderDTO), OrderDTO.class))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
