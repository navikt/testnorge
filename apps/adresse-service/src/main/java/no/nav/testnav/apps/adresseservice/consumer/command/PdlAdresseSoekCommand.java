package no.nav.testnav.apps.adresseservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.adresseservice.dto.GraphQLRequest;
import no.nav.testnav.apps.adresseservice.dto.PdlAdresseResponse;
import no.nav.testnav.apps.adresseservice.exception.BadRequestException;
import no.nav.testnav.apps.adresseservice.exception.NotFoundException;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlAdresseSoekCommand implements Callable<Mono<PdlAdresseResponse>> {

    private static final String TEMA = "Tema";

    private final WebClient webClient;
    private final GraphQLRequest query;
    private final String token;

    @Override
    public Mono<PdlAdresseResponse> call() {

        return webClient
                .post()
                .uri(builder -> builder.path("/pdl-api/graphql").build())
                .body(BodyInserters.fromValue(query))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, TemaGrunnlag.GEN.name())
                .retrieve()
                .bodyToMono(PdlAdresseResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .map(value -> {
                    if (!value.getErrors().isEmpty()) {
                        throw new BadRequestException("Spørring inneholder feil: " + value.getErrors().toString());
                    } else if (value.getData().getSokAdresse().getHits().isEmpty()) {
                        throw new NotFoundException("Ingen adresse funnet: " + query.getVariables().get("criteria"));
                    } else {
                        return value;
                    }
                });
    }

    private enum TemaGrunnlag {GEN, PEN}
}
