package no.nav.registre.sdforvalter.consumer.rs.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.aareg.response.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_ARBEIDSFORHOLD;

@RequiredArgsConstructor
@Slf4j
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
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(arbeidsforhold))
                .retrieve()
                .bodyToMono(Arbeidsforhold.class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold1)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .arbeidsforhold(arbeidsforhold)
                        .arbeidsforholdId(arbeidsforhold.getArbeidsforholdId())
                        .miljo(miljoe)
                        .error(error)
                        .build()));
    }
}
