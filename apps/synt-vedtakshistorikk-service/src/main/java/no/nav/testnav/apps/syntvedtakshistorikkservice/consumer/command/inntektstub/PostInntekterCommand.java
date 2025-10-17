package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.inntektstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.inntektstub.Inntektsinformasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
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
                .uri(uriBuilder -> uriBuilder.path("/inntektstub/api/v2/inntektsinformasjon").build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log));
    }

}
