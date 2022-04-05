package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Order(2)
@RequiredArgsConstructor
public class HendelseslagerPublishCommand implements Callable<Mono<String>> {

    private static final String PDL_PUBLISH = "/internal/publish";
    private static final String PDL_IDENTHENDELSE = "/pdl-identhendelse";
    private static final String PERSON_IDENTER = "Nav-Personidenter";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<String> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_IDENTHENDELSE)
                        .path(PDL_PUBLISH)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(PERSON_IDENTER, identer.stream().collect(Collectors.joining(",")))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
