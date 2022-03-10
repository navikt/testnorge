package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.utils.WebClientFilter;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GenererNavnServiceCommand implements Callable<Mono<NavnDTO[]>> {

    private final WebClient webClient;
    private final String url;
    private final Integer antall;
    private final String token;

    @Override
    public Mono<NavnDTO[]> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(url).queryParam("antall", antall).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(NavnDTO[].class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
