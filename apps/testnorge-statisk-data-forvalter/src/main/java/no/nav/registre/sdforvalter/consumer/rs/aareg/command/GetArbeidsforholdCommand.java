package no.nav.registre.sdforvalter.consumer.rs.aareg.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.aareg.response.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import no.nav.registre.sdforvalter.util.WebClientFilter;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

@RequiredArgsConstructor
@Slf4j
public class GetArbeidsforholdCommand implements Callable<Mono<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidstaker/arbeidsforhold";
    private static final String ARBEIDSFORHOLD_TYPE = "arbeidsforholdtype";
    public static final String ARBEIDSFORHOLD_AVAIL =
            "forenkletOppgjoersordning, " +
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Arbeidsforhold[].class)
                .map(arbeidsforhold1 -> ArbeidsforholdRespons.builder()
                        .eksisterendeArbeidsforhold(Arrays.asList(arbeidsforhold1))
                        .miljo(miljoe)
                        .build())
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .miljo(miljoe)
                        .error(error)
                        .build()));
    }
}
