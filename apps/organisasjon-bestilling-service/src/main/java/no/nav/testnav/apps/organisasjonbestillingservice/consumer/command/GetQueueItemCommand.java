package no.nav.testnav.apps.organisasjonbestillingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.organisasjonbestillingservice.consumer.dto.ItemDTO;
import no.nav.testnav.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetQueueItemCommand implements Callable<Mono<ItemDTO>> {
    private final WebClient webClient;
    private final String token;
    private final JenkinsCrumb crumb;
    private final Long itemId;

    @Override
    public Mono<ItemDTO> call() {
        log.info("Henter jobb fra Jenkins med id: {}.", itemId);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/queue/item/{itemId}/api/json").build(itemId))
                .header(crumb.getCrumbRequestField(), crumb.getCrumb())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ItemDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
