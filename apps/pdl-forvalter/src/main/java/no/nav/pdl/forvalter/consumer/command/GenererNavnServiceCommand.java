package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GenererNavnServiceCommand implements Callable<Flux<NavnDTO>> {

    private final WebClient webClient;
    private final String url;
    private final Integer antall;
    private final String token;

    @Override
    public Flux<NavnDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path(url)
                        .queryParam("antall", antall)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(NavnDTO[].class)
                .flatMapMany(Flux::fromArray)
                .retryWhen(WebClientError.is5xxException());
    }
}
