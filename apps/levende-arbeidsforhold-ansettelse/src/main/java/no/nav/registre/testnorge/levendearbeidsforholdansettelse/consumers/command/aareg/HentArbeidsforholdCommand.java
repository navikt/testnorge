package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.Arbeidsforhold;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.servletcore.headers.NavHeaders;
import no.nav.testnav.libs.servletcore.provider.InternalController;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.String.format;


@Slf4j
@RequiredArgsConstructor
public class HentArbeidsforholdCommand implements Callable<List<Arbeidsforhold>> {
    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String miljoe = "q2";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String CONSUMER = "Dolly";


    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @SneakyThrows
    @Override
    public List<Arbeidsforhold> call(){

        try {
            var arbeidsforhold = webClient
                .get()
                .uri(builder -> builder
                        .path("/{miljoe}/api/v1/arbeidstaker/arbeidsforhold")
                        .queryParam("arbeidsforholdtype", "forenkletOppgjoersordning", "frilanserOppdragstakerHonorarPersonerMm", "maritimtArbeidsforhold", "ordinaertArbeidsforhold")
                        .build(miljoe))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(NAV_PERSON_IDENT, ident)
                .header(NavHeaders.NAV_CONSUMER_ID, CONSUMER)
                .header(NavHeaders.NAV_CALL_ID, getNavCallId())
                .retrieve()
                .bodyToMono(Arbeidsforhold[].class)
                .retryWhen(Retry
                        .backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                    .block();
            //log.info("Json object i command {}", Json.pretty(arbeidsforhold));
            assert arbeidsforhold != null;
            return Arrays.stream(arbeidsforhold).collect(Collectors.toList());
        } catch (WebClientResponseException.NotFound e) {
            return Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold. Feilmelding: {}.",
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
