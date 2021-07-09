package no.nav.registre.testnorge.synt.sykemelding.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v1.ArbeidsforholdDTO;

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
                .uri(builder -> builder
                        .path("/api/v1/arbeidsforhold/{ident}/{orgnummer}/{arbeidsforholdId}")
                        .build(ident, orgnummer, arbeidsforholdId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("miljo", "q1")
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response -> Mono.error(
                        new HttpClientErrorException(HttpStatus.NOT_FOUND, "Fant ikke arbeidsforhold")))
                .bodyToMono(ArbeidsforholdDTO.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(10)))
                .block();
    }
}