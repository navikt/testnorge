package no.nav.dolly.bestilling.histark;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.histark.command.HistarkPostCommand;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class HistarkConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public HistarkConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {
        serverProperties = consumers.getTestnavHistarkProxy();
        this.tokenService = tokenService;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .filters(exchangeFilterFunctions -> exchangeFilterFunctions.add(logRequest()))
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "histark-opprett" })
    public Flux<HistarkResponse> postHistark(HistarkRequest histarkRequest) {

        var callId = getNavCallId();
        log.info("Sender histark melding: callId: {}, consumerId: {}\n{}", callId, CONSUMER, histarkRequest);

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new HistarkPostCommand(webClient, histarkRequest,
                        token.getTokenValue()).call());
    }

    private ExchangeFilterFunction logRequest() {

        return (clientRequest, next) -> {
            var buffer = new StringBuilder(250)
                    .append("Request: ")
                    .append(clientRequest.method())
                    .append(' ')
                    .append(clientRequest.url())
                    .append(System.lineSeparator());

            clientRequest.headers()
                    .forEach((name, values) -> values
                            .forEach(value -> buffer.append('\t')
                                    .append(name)
                                    .append('=')
                                    .append(nonNull(value) && value.contains("Bearer ") ? "Bearer token" : value)
                                    .append(System.lineSeparator())));
            log.trace(buffer.substring(0, buffer.length() - 1));
            return next.exchange(clientRequest);
        };
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}
