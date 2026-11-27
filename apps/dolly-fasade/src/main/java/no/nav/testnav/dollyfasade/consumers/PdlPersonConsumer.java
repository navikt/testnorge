package no.nav.testnav.dollyfasade.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollyfasade.config.Consumers;
import no.nav.testnav.dollyfasade.consumers.command.PdlPersoneMiljoeGetCommand;
import no.nav.testnav.dollyfasade.consumers.command.PdlPersonerGetCommand;
import no.nav.testnav.libs.dto.personservice.v1.PdlMiljoer;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static no.nav.testnav.dollyfasade.utility.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlPersonConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            JsonMapper jsonMapper,
            WebClient webClient) {

        serverProperties = consumers.getPersonService();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(jsonMapper))
                .build();
    }

    public Mono<JsonNode> getPdlPerson(String ident, PdlMiljoer pdlMiljoe) {

        return tokenService.exchange(serverProperties)
                .flatMap((AccessToken token) -> new PdlPersoneMiljoeGetCommand(webClient, ident, pdlMiljoe.name(), token.getTokenValue())
                        .call());
    }

    public Flux<JsonNode> getPdlPersoner(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new PdlPersonerGetCommand(webClient, identer, token.getTokenValue()).call());
    }
}
