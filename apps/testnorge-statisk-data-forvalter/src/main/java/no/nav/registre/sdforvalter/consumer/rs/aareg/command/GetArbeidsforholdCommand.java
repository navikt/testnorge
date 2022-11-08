package no.nav.registre.sdforvalter.consumer.rs.aareg.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.consumer.rs.aareg.response.ArbeidsforholdRespons;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;

@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<Mono<ArbeidsforholdRespons>> {

    private static final String AAREGDATA_URL = "/{miljoe}/api/v1/arbeidstaker/arbeidsforhold";
    private static final String ARBEIDSFORHOLD_TYPE = "arbeidsforholdtype";
    private static final String ARBEIDSFORHOLD_AVAIL =
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
                .onErrorResume(error -> Mono.just(ArbeidsforholdRespons.builder()
                        .miljo(miljoe)
                        .error(error)
                        .build()));
    }
}
