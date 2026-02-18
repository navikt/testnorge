package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.OrderDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class UpdateOrganisasjonBestillingCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final OrderDTO orderDTO;
    private final String token;
    private final String uuid;
    private final Long id;

    @Override
    public Mono<Void> call() {
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/order/{uuid}/items/{id}").build(uuid, id))
                .bodyValue(orderDTO)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Void.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(WebClientError.is5xxException());
    }

}
