package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.sykemelding.command.Norg2GetCommand;
import no.nav.dolly.bestilling.sykemelding.dto.Norg2EnhetResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class Norg2Consumer implements ConsumerStatus {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;

    public Norg2Consumer(
            TokenExchange accessTokenService,
            Consumers.Norg2Proxy serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "detaljertsykemelding_opprett" })
    public Mono<Norg2EnhetResponse> getNorgEnhet(String geografiskTilhoerighet) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new Norg2GetCommand(webClient, geografiskTilhoerighet,
                        token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-norg2-proxy";
    }
}
