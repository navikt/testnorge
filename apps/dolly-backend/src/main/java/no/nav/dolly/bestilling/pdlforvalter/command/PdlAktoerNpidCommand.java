package no.nav.dolly.bestilling.pdlforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class PdlAktoerNpidCommand implements Callable<Mono<Void>> {

    private static final String PDL_AKTOER_ADMIN_PREFIX = "/pdl-npid";
    private static final String PDL_PERSON_AKTOER_URL = PDL_AKTOER_ADMIN_PREFIX + "/api/npid";

    private final WebClient webClient;
    private final String npid;
    private final String token;

    public Mono<Void> call() {

        return webClient
                .post()
                .uri(PDL_PERSON_AKTOER_URL)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(npid))
                .retrieve()
                .bodyToMono(Void.class);
    }
}