package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .queryParam(NORSKE_IDENTER_QUERY, ident)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
