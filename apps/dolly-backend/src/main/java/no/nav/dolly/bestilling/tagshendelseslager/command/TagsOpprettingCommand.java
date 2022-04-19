package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class TagsOpprettingCommand implements Callable<Flux<String>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String TAGS = "tags";

    private final WebClient webClient;
    private final List<String> identer;
    private final List<Tags> tagVerdier;
    private final String token;

    public Flux<String> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .queryParam(TAGS, tagVerdier)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(identer))
                .retrieve()
                .bodyToFlux(String.class);
//                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
//                        .filter(WebClientFilter::is5xxException));
    }
}
