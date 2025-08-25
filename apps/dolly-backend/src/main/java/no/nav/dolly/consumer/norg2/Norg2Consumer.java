package no.nav.dolly.consumer.norg2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.consumer.norg2.command.Norg2GetCommand;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class Norg2Consumer extends ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public Norg2Consumer(
            TokenExchange accessTokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = accessTokenService;
        serverProperties = consumers.getTestnavNorg2Proxy();
        this.webClient = webClient
                .mutate()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "detaljertsykemelding_opprett"})
    public Mono<Norg2EnhetResponse> getNorgEnhet(String geografiskTilhoerighet) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new Norg2GetCommand(webClient, geografiskTilhoerighet,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-norg2-proxy";
    }
}
