package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.aareg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;
@Slf4j
@RequiredArgsConstructor
public class OpprettArbeidsforholdCommand implements Callable<Mono<ResponseEntity<Void>>> {

    private final String navArbeidsforholdKilde = "Dolly-automatisk-ansettelse" ;
    private final String miljoe = "q2";

    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;

    @Override
    public Mono<ResponseEntity<Void>> call() {

        return webClient.post()
                .uri(builder -> builder.path("/{miljoe}/api/v1/arbeidsforhold")
                        .build(miljoe))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Arbeidsforhold-Kildereferanse", navArbeidsforholdKilde)
                .bodyValue(requests)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
