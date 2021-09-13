package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.IdentDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolPostCommand implements Callable<Mono<List<IdentDTO>>> {


    private final WebClient webClient;
    private final String url;
    private final String query;
    private final Object body;
    private final String token;

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException ?
                ((WebClientResponseException) error).getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Mono<List<IdentDTO>> call() {

        return webClient
                .post()
                .uri(builder -> builder.path(url).query(query).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .flatMap(identer -> Mono.just(Arrays.asList(identer).stream()
                        .map(ident -> IdentDTO.builder()
                                .ident(ident)
                                .build())
                                .map(IdentDTO.class::cast)
                        .collect(Collectors.toList())))
                .doOnError(throwable -> log.error(getMessage(throwable)));
    }
}
