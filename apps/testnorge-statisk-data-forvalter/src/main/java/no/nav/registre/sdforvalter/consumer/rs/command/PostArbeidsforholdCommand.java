package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.consumer.rs.domain.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;

@RequiredArgsConstructor
public class PostArbeidsforholdCommand implements Callable<Mono<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidsforhold";
    private final WebClient webClient;
    private final String miljoe;
    private final Arbeidsforhold arbeidsforhold;
    private final String token;

    @Override
    public Mono<ArbeidsforholdRespons> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .build(miljoe))
                .header(HEADER_NAV_ARBEIDSFORHOLD, CallIdUtil.generateCallId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToMono(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .build())
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .error(error)
                        .build()));
    }
}
