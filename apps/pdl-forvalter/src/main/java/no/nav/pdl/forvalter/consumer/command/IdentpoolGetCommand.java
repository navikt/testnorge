package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.dto.IdentpoolStatusDTO;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.pdl.forvalter.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolGetCommand implements Callable<Mono<IdentpoolStatusDTO>> {

    private static final String IDENTPOOL = "Identpool: ";
    private static final String IDENT = "personidentifikator";

    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;

    protected static String getMessage(Throwable error) {

        return error instanceof WebClientResponseException webClientResponseException ?
                webClientResponseException.getResponseBodyAsString() :
                error.getMessage();
    }

    @Override
    public Mono<IdentpoolStatusDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(url).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(IDENT, ident)
                .retrieve()
                .bodyToMono(IdentpoolStatusDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {
                    log.error(getMessage(throwable));
                    if (throwable instanceof WebClientResponseException) {
                        if (((WebClientResponseException) throwable).getStatusCode() == HttpStatus.NOT_FOUND) {
                            return Mono.error(new NotFoundException(IDENTPOOL + getMessage(throwable)));
                        } else {
                            return Mono.error(new InvalidRequestException(IDENTPOOL + getMessage(throwable)));
                        }
                    } else {
                        return Mono.error(new InternalError(IDENTPOOL + getMessage(throwable)));
                    }
                });
    }
}
