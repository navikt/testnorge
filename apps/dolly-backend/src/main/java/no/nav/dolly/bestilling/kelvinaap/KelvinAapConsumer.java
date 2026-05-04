package no.nav.dolly.bestilling.kelvinaap;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.kelvinaap.command.AapOpprettOgFullfoerPostCommand;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettResponse;
import no.nav.dolly.config.Consumers;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class KelvinAapConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;

    public KelvinAapConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            JsonMapper jsonMapper,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(jsonMapper))
                .build();
    }

    public Mono<AapOpprettResponse>  createAap(AapOpprettRequest request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token ->
                        new AapOpprettOgFullfoerPostCommand(webClient, request, token.getTokenValue()).call());
    }
}
