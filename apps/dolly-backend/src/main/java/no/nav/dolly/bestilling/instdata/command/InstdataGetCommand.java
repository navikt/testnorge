package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataRequest;
import no.nav.dolly.bestilling.instdata.domain.InstitusjonsoppholdRespons;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@RequiredArgsConstructor
public class InstdataGetCommand implements Callable<Mono<InstitusjonsoppholdRespons>> {

    private static final String INSTDATA_URL = "/api/v1/institusjonsopphold/person/soek";

    private static final String INST_MILJO = "environments";
    private static final String INST_IDENT = "norskident";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<InstitusjonsoppholdRespons> call() {
        return webClient
                .post()
                .uri(builder -> builder.path(INSTDATA_URL)
                        .queryParam(INST_MILJO, miljoe)
                        .build())
                .header(INST_IDENT, ident)
                .header(HttpHeaders.ACCEPT, "application/json")
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .bodyValue(InstdataRequest.builder()
                        .personident(ident)
                        .environments(List.of(miljoe))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<Instdata>>>() {
                })
                .map(resultat -> InstitusjonsoppholdRespons.builder()
                        .institusjonsopphold(resultat)
                        .build())
                .doOnError(error -> log.error("Henting av institusjonsopphold feilet", error))
                .onErrorResume(error -> Mono.just(new InstitusjonsoppholdRespons()))
                .retryWhen(WebClientError.is5xxException());
    }

}
