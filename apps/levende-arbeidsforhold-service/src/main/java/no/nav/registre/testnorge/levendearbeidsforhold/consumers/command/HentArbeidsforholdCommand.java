package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * Command for å hente arbeidsforhold fra Aareg.
 * Brukt for å hente arbeidsforhold for en arbeidstaker.
 * WebClient er konfigurert med en base url som er satt i konfigurasjonen.
 * Token er en jwt token som brukes for å autentisere mot Aareg.
 * Id er identen til arbeidstakeren som skal hentes arbeidsforhold for.
 * Hvis det ikke finnes arbeidsforhold for identen vil en WebClientResponseException.NotFound bli kastet.
 * Hvis det er en annen feil vil en WebClientResponseException bli kastet.
 * Hvis det er en 5xx feil vil den prøve å hente på nytt 3 ganger med 5 sekunders intervall.
 * Hvis det fortsatt feiler vil en WebClientResponseException bli kastet.
 * Hvis alt går bra vil en liste med ArbeidsforholdDTO bli returnert.
 */
@Slf4j
@RequiredArgsConstructor
public class HentArbeidsforholdCommand implements Callable<List<ArbeidsforholdDTO>> {
    private final WebClient webClient;
    private final String token;
    private final String id;
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String CONSUMER = "Dolly";


    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @SneakyThrows
    @Override
    public List<ArbeidsforholdDTO> call(){
        //Dette er metoden som er krevet når man implementerer Callable
        log.info("Henter arbeidsforhold for ident {}.", id);
        try {
            //Bygger request URL og henter arneidsforhold fra Aareg med path = /api/v1/arbeidstaker/arbeidsforhold
            var arbeidsforhold = webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/arbeidstaker/arbeidsforhold")
                            .build(id)
                    )
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(NAV_PERSON_IDENT, id)
                    .header("Nav-Call-Id", getNavCallId())
                    .header("Nav-Consumer-Id", CONSUMER)
                    .retrieve()
                    .bodyToMono(ArbeidsforholdDTO[].class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
            log.info("Hentet arbeidsforhold fra Aareg: " + Arrays.toString(arbeidsforhold));
            //Returnerer en liste med ArbeidsforholdDTO
            return Arrays.stream(arbeidsforhold).collect(Collectors.toList());
        } catch (WebClientResponseException.NotFound e) {
            //Hvis det ikke finnes arbeidsforhold for identen vil en WebClientResponseException.NotFound bli kastet.
            log.warn("Fant ikke arbeidsforhold for ident {}. Feilmelding {}",
                    id,
                    e.getResponseBodyAsString());
            return List.of();
        } catch (WebClientResponseException e) {
            //Hvis det er en annen feil vil en WebClientResponseException bli kastet.
            log.error(
                    "Klarer ikke å hente arbeidsforhold for ident: {}. Feilmelding: {}.",
                    id,
                    e.getResponseBodyAsString()
            );
            throw e;
        }
    }
}
