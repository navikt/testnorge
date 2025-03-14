package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class PostInntekterCommand implements Callable<Mono<List<Inntektsinformasjon>>> {

    private final List<Inntektsinformasjon> inntektsinformasjon;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Inntektsinformasjon>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<Inntektsinformasjon>> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/inntektsinformasjon").build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientFilter::logErrorMessage);
    }

}
