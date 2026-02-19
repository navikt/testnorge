package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonOrgnummerServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class OrganisasjonOrgnummerServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public OrganisasjonOrgnummerServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient) {
        serverProperties = consumers.getTestnavOrgnummerService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Mono<List<String>> getOrgnummer(Integer antall) {

        long startTime = currentTimeMillis();
        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new OrganisasjonOrgnummerServiceCommand(webClient, antall, token.getTokenValue()).call())
                .map(response -> {
                    log.info("Orgnummer-service svarte etter {} ms", currentTimeMillis() - startTime);
                    return Stream.of(response).toList();
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error(e.getMessage(), e);
                    return new ResponseStatusException(e.getStatusCode(), e.getMessage());
                })
                .onErrorMap(RuntimeException.class, e -> {
                    String error = format("Testnav-orgnummer-service svarte ikke etter %d ms", currentTimeMillis() - startTime);
                    log.error(error, e);
                    return new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, error);
                });
    }

    public Mono<String> getOrgnummer() {

        return getOrgnummer(1)
                .flatMap(orgnummer -> Mono.justOrEmpty(orgnummer.isEmpty() ? null : orgnummer.get(0)));
    }
}
