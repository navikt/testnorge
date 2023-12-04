package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstitusjonsoppholdRespons;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class InstdataGetCommand implements Callable<Mono<InstitusjonsoppholdRespons>> {

    private static final String INSTDATA_URL = "/api/v1/institusjonsopphold/person";

    private static final String INST_MILJO = "environments";
    private static final String INST_IDENT = "norskident";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstitusjonsoppholdRespons> call() {

        return webClient.get()
                .uri(builder -> builder.path(INSTDATA_URL)
                        .queryParam(INST_MILJO, miljoe)
                        .build())
                .header(INST_IDENT, ident)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<Instdata>>>() {
                })
                .map(resultat -> InstitusjonsoppholdRespons.builder()
                        .institusjonsopphold(resultat)
                        .build())
                .doOnError(error -> log.error("Henting av institusjonsopphold feilet", error))
                .onErrorResume(error -> Mono.just(new InstitusjonsoppholdRespons()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
