package no.nav.registre.testnav.inntektsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.inntektsmeldingservice.util.WebClientFilter;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Callable;


@Slf4j
@RequiredArgsConstructor
public class GenererInntektsmeldingCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final RsInntektsmelding dto;
    private final String token;

    @Override
    public Mono<String> call() {
        return webClient
                .post()
                .uri("/api/v2/inntektsmelding/2018/12/11")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .acceptCharset(StandardCharsets.UTF_8)
                .body(BodyInserters.fromPublisher(Mono.just(dto), RsInntektsmelding.class))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
