package no.nav.dolly.bestilling.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

@RequiredArgsConstructor
@Slf4j
public class ArbeidsforholdGetCommand implements Callable<Mono<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidstaker/arbeidsforhold";
    private static final String ARBEIDSFORHOLD_TYPE = "arbeidsforholdtype";
    private static final String ARBEIDSFORHOLD_AVAIL = "forenkletOppgjoersordning, " +
            "frilanserOppdragstakerHonorarPersonerMm, " +
            "maritimtArbeidsforhold, " +
            "ordinaertArbeidsforhold";
    private static final String HISTORIKK = "historikk";

    private final WebClient webClient;
    private final String miljoe;
    private final String ident;
    private final String token;

    @Override
    public Mono<ArbeidsforholdRespons> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .queryParam(ARBEIDSFORHOLD_TYPE, ARBEIDSFORHOLD_AVAIL)
                        .queryParam(HISTORIKK, true)
                        .build(miljoe))
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Arbeidsforhold[].class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .eksisterendeArbeidsforhold(Arrays.asList(arbeidsforhold1))
                        .miljoe(miljoe)
                        .build())
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .miljoe(miljoe)
                        .error(error)
                        .build()));
    }
}