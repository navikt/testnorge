package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.util.TokenXUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InntektstubPostCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";

    private final WebClient webClient;
    private final List<Inntektsinformasjon> inntektsinformasjon;
    private final String token;

    @Override
    public Flux<Inntektsinformasjon> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, TokenXUtil.getUserJwt())
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .onErrorResume(error -> {
                    log.error("Lagring av Instdata feilet: {}", WebClientFilter.getMessage(error), error);
                    return Flux.just(Inntektsinformasjon.builder()
                            .feilmelding(WebClientFilter.getMessage(error))
                            .build());
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
