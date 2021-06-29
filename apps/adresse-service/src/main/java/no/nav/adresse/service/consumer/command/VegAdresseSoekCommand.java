package no.nav.adresse.service.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.adresse.service.consumer.PdlAdresseConsumer.TemaGrunnlag;
import no.nav.adresse.service.dto.GraphQLRequest;
import no.nav.adresse.service.dto.VegAdresseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.adresse.service.consumer.PdlAdresseConsumer.TEMA;

@Slf4j
@RequiredArgsConstructor
public class VegAdresseSoekCommand implements Callable<Mono<VegAdresseResponse>> {

    private final WebClient webClient;
    private final GraphQLRequest query;
    private final String token;

    @Override
    public Mono<VegAdresseResponse> call() {

        return webClient
                .post()
                .uri(builder -> builder.path("/pdl-api/graphql").build())
                .body(BodyInserters.fromValue(query))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, TemaGrunnlag.GEN.name())
                .retrieve()
                .bodyToMono(VegAdresseResponse.class);
    }
}
