package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class TagsSlettingCommand implements Callable<Mono<String>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String TAGS = "tags";

    private final WebClient webClient;
    private final List<String> tagVerdier;
    private final String token;

    //TODO denne trenger oppdatering når nytt endepunkt er klart
    public Mono<String> call() {

        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .queryParam(TAGS, tagVerdier)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
//                .body(BodyInserters.fromValue(identer))
                .retrieve()
                .bodyToMono(String.class);
    }
}
