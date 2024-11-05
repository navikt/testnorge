package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.altinn3tilgangservice.consumer.altinn.dto.ServiceResourceDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetResourceCommand implements Callable<Mono<ServiceResourceDTO>> {

    private static final String ALTINN_RESOURCE_URL = "/resource/{id}";
    private final WebClient webClient;
    private final String token;
    private final String id;

    @Override
    public Mono<ServiceResourceDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(ALTINN_RESOURCE_URL)
                        .build(id)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ServiceResourceDTO.class)
                .doOnError(
                        WebClientResponseException.class::isInstance,
                        throwable -> log.error(
                                "Feil ved henting av rettigheter i Altinn. {}",
                                ((WebClientResponseException) throwable).getResponseBodyAsString()));
    }
}
