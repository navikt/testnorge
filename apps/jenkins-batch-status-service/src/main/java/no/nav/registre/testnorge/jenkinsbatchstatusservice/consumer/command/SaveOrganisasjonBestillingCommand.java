package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SaveOrganisasjonBestillingCommand implements Callable<Long> {
    private final WebClient webClient;
    private final String token;
    private final String uuid;

    @Override
    public Long call() {
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/order/{uuid}").build(uuid))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Long.class)
                .timeout(Duration.ofSeconds(10))
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
