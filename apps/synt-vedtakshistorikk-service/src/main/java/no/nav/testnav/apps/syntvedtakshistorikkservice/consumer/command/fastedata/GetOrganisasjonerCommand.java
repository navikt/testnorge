package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.fastedata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.fastedata.Organisasjon;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetOrganisasjonerCommand implements Callable<Mono<List<Organisasjon>>> {

    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Organisasjon>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<Organisasjon>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("api/v1/organisasjoner")
                        .queryParam("gruppe", "DOLLY")
                        .queryParam("kanHaArbeidsforhold", "true")
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
