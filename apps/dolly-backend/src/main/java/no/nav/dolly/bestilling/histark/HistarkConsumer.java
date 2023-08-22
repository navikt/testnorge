package no.nav.dolly.bestilling.histark;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.histark.command.HistarkPostCommand;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.config.credentials.HistarkProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class HistarkConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public HistarkConsumer(
            HistarkProxyProperties properties,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {
        this.serviceProperties = properties;
        this.tokenService = tokenService;
        this.webClient = webClientBuilder
                .baseUrl(properties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "dokarkiv-opprett" })
    public Flux<HistarkResponse> postHistark(HistarkRequest histarkRequest) {

        var callId = getNavCallId();
        log.info("Sender histark melding: callId: {}, consumerId: {}\n{}", callId, CONSUMER, histarkRequest);

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new HistarkPostCommand(webClient, histarkRequest,
                        token.getTokenValue()).call());
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}
