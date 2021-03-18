package no.nav.registre.testnorge.synt.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetArbeidsforholdCommand implements Callable<ArbeidsforholdDTO> {
    private final WebClient webClient;
    private final String accessToken;
    private final String ident;
    private final String orgnummer;
    private final String arbeidsforholdId;

    @SneakyThrows
    @Override
    public ArbeidsforholdDTO call() {

        return webClient
                .get()
                .uri("/api/v1/arbeidsforhold/" + ident + "/" + orgnummer + "/" + arbeidsforholdId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> response
                        .bodyToMono(ArbeidsforholdDTO.class)
                        .flatMap(error -> Mono.error(new HttpClientErrorException(HttpStatus.NOT_FOUND, String.format("Fant ikke arbeidsforhold for %s i organisasjon %s", ident, arbeidsforholdId)))))
                .bodyToMono(ArbeidsforholdDTO.class)
                .block();
    }
}
