package no.nav.registre.testnorge.arbeidsforholdservice.consumer.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.servletcore.headers.NavHeaders;
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
public class GetArbeidstakerArbeidsforholdCommand implements Callable<List<ArbeidsforholdDTO>> {
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    private static final String CONSUMER = "Dolly";
    private final WebClient webClient;
    private final String miljo;
    private final String token;
    private final String ident;

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @SneakyThrows
    @Override
    public List<ArbeidsforholdDTO> call() {
        try {
            var arbeidsforhold = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/{miljo}/v1/arbeidstaker/arbeidsforhold")
                            .queryParam("arbeidsforholdtype", "forenkletOppgjoersordning", "frilanserOppdragstakerHonorarPersonerMm", "maritimtArbeidsforhold", "ordinaertArbeidsforhold")
                            .build(miljo))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(NAV_PERSON_IDENT, ident)
                    .header(NavHeaders.NAV_CONSUMER_ID, CONSUMER)
                    .header(NavHeaders.NAV_CALL_ID, getNavCallId())
                    .retrieve()
                    .bodyToMono(ArbeidsforholdDTO[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            log.info("Hentet arbeidsforhold fra Aareg: " + Arrays.toString(arbeidsforhold));
            return Arrays.stream(arbeidsforhold).collect(Collectors.toList());
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke arbeidsforhold for ident {} i miljø {}", ident, miljo);
            return Collections.emptyList();
        } catch (WebClientResponseException e) {
            log.error(
                    "Klarer ikke å hente arbeidsforhold for ident: {}. Feilmelding: {}.",
                    ident,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
