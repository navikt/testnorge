package no.nav.dolly.bestilling.tagshendelseslager.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class TagsHenteCommand implements Callable<Mono<JsonNode>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String IDENT = "Nav-Personident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Mono<JsonNode> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(IDENT, ident)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
