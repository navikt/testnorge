package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InntektstubPostCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/inntektstub/api/v2/inntektsinformasjon";

    private final WebClient webClient;
    private final List<Inntektsinformasjon> inntektsinformasjon;
    private final String token;

    @Override
    public Flux<Inntektsinformasjon> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Lagring av Instdata feilet: {}", description.getMessage(), throwable);
                    return Inntektsinformasjon.of(description);
                });
    }
}