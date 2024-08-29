package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.servletcore.headers.NavHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.lang.String.format;


@Slf4j
@RequiredArgsConstructor
public class HentArbeidsforholdCommand implements Callable<Flux<Arbeidsforhold>> {

    private static final String miljoe = "q2";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final String token;
    private final String ident;

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @Override
    public Flux<Arbeidsforhold> call() {

        return webClient
                .get()
                .uri(builder -> builder
                        .path("/{miljoe}/api/v1/arbeidstaker/arbeidsforhold")
                        .queryParam("arbeidsforholdtype", "forenkletOppgjoersordning",
                                "frilanserOppdragstakerHonorarPersonerMm", "maritimtArbeidsforhold",
                                "ordinaertArbeidsforhold")
                        .build(miljoe))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(NAV_PERSON_IDENT, ident)
                .header(NavHeaders.NAV_CONSUMER_ID, CONSUMER)
                .header(NavHeaders.NAV_CALL_ID, getNavCallId())
                .retrieve()
                .bodyToFlux(Arbeidsforhold.class)
                .retryWhen(Retry
                        .backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(WebClientResponseException.NotFound.class, error -> Mono.empty())
                .onErrorResume(WebClientResponseException.class, error -> Mono.empty());
    }
}