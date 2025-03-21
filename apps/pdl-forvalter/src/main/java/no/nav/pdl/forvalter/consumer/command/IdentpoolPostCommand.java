package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.IdentDTO;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolPostCommand implements Callable<Mono<List<IdentDTO>>> {

    private static final String IDENTPOOL = "Identpool: ";

    private final WebClient webClient;
    private final String url;
    private final String query;
    private final Object body;
    private final String token;

    @Override
    public Mono<List<IdentDTO>> call() {
        return webClient
                .post()
                .uri(builder -> builder.path(url).query(query).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String[].class)
                .flatMap(identer -> Mono.just(Arrays.stream(identer)
                        .map(ident -> IdentDTO.builder()
                                .ident(ident)
                                .build())
                        .map(IdentDTO.class::cast)
                        .toList()))
                .retryWhen(WebClientError.is5xxExceptionThen(new InternalError(IDENTPOOL + "antall repeterende forsøk nådd")))
                .onErrorResume(throwable -> {
                    log.error(getMessage(throwable));
                    if (throwable instanceof WebClientResponseException exception) {
                        if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new NotFoundException(IDENTPOOL + getMessage(throwable)));
                        } else {
                            return Mono.error(new InvalidRequestException(IDENTPOOL + getMessage(throwable)));
                        }
                    } else {
                        return Mono.error(new InternalError(IDENTPOOL + getMessage(throwable)));
                    }
                });
    }

    protected static String getMessage(Throwable error) {
        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                error.getMessage();
    }

}
