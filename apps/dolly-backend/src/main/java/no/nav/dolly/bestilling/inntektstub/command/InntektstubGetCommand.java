package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class InntektstubGetCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";
    private static final String NORSKE_IDENTER_QUERY = "norske-identer";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<Inntektsinformasjon> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
