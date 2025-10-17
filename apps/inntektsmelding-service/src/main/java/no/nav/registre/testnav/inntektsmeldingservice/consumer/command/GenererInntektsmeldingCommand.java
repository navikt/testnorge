package no.nav.registre.testnav.inntektsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;


@Slf4j
@RequiredArgsConstructor
public class GenererInntektsmeldingCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final RsInntektsmelding dto;
    private final String token;

    @Override
    public Mono<String> call() {
        log.info("Generer XML-inntektsmelding fra: {}", dto);
        return webClient
                .post()
                .uri("/api/v2/inntektsmelding/2018/12/11")
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
