package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class InntektstubGetCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";
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
                .bodyToFlux(Inntektsinformasjon.class);
    }
}