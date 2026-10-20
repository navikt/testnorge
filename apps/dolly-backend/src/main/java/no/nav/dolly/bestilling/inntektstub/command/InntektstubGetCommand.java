package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InntektstubGetCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/inntektstub/api/v2/inntektsinformasjon";
    private static final String NORSKE_IDENTER_QUERY = "norske-identer";
    private static final String HISTORISK_QUERY = "historikk";

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
                        .queryParam(HISTORISK_QUERY, true)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Henting av data fra Inntektstub feilet: {}", description.getMessage(), throwable);
                    return Inntektsinformasjon.of(description);
                });
    }
}