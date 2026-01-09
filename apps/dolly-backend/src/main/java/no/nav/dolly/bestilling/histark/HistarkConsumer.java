package no.nav.dolly.bestilling.histark;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.histark.command.HistarkPostCommand;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class HistarkConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public HistarkConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient webClient) {

        serverProperties = consumers.getTestnavDollyProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "histark-opprett"})
    public Mono<HistarkResponse> postHistark(HistarkRequest histarkRequest) {

        var callId = getNavCallId();
        log.info("Sender histark melding: callId: {}, consumerId: {}\n{}", callId, CONSUMER, histarkRequest);

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HistarkPostCommand(webClient, histarkRequest,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-dolly-proxy";
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
