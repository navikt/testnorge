package no.nav.registre.testnorge.generersyntameldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.generersyntameldingservice.util.WebClientFilter;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostHentArbeidsforholdCommand implements Callable<Mono<Arbeidsforhold>> {

    private final WebClient webClient;
    private final ArbeidsforholdPeriode periode;
    private final String arbeidsforholdType;
    private final String token;

    @Override
    public Mono<Arbeidsforhold> call() {
        var body = BodyInserters.fromPublisher(Mono.just(periode), ArbeidsforholdPeriode.class);

        return webClient.post()
                .uri(builder -> builder
                        .path("/api/v1/arbeidsforhold/start/{arbeidsforholdType}")
                        .queryParam("avvik", "false")
                        .build(arbeidsforholdType))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(body)
                .retrieve()
                .bodyToMono(Arbeidsforhold.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }

}
