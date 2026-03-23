package no.nav.testnav.levendearbeidsforholdservice.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.concurrent.Callable;

import static java.lang.String.format;

@RequiredArgsConstructor
@Slf4j
public class HentArbeidsforholdCommand implements Callable<Flux<Arbeidsforhold>> {

    private static final String MILJOE = "q2";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "Nav-Call-Id";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final String token;
    private final String ident;

    @SneakyThrows
    @Override
    public Flux<Arbeidsforhold> call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/aareg/{miljoe}/api/v1/arbeidstaker/arbeidsforhold")
                        .queryParam("arbeidsforholdtype", "forenkletOppgjoersordning",
                                "frilanserOppdragstakerHonorarPersonerMm", "maritimtArbeidsforhold",
                                "ordinaertArbeidsforhold")
                        .build(MILJOE))
                .headers(WebClientHeader.bearer(token))
                .header(NAV_PERSON_IDENT, ident)
                .header(NAV_CONSUMER_ID, CONSUMER)
                .header(NAV_CALL_ID, getNavCallId())
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Flux.empty());
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}
