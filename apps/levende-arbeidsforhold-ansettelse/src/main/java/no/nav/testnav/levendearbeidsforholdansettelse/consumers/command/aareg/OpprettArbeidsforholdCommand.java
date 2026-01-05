package no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.ArbeidsforholdResponseDTO;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class OpprettArbeidsforholdCommand implements Callable<Mono<ArbeidsforholdResponseDTO>> {

    private static final String MILJOE = "q2";

    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;

    @Override
    public Mono<ArbeidsforholdResponseDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/aareg/{miljoe}/api/v1/arbeidsforhold")
                        .build(MILJOE))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .header("Nav-Arbeidsforhold-Kildereferanse", "Dolly-automatisk-ansettelse")
                .bodyValue(requests)
                .retrieve()
                .bodyToMono(String.class)
                .map(payload -> ArbeidsforholdResponseDTO
                        .builder()
                        .statusCode(HttpStatusCode.valueOf(HttpStatus.CREATED.value()))
                        .payload(payload)
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(error -> ArbeidsforholdResponseDTO.of(WebClientError.describe(error)));
    }

}
