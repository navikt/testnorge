package no.nav.testnav.dollysearchservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.config.Consumers;
import no.nav.testnav.dollysearchservice.consumer.command.OpenSearchCommand;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static no.nav.testnav.dollysearchservice.consumer.utils.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class OpenSearchConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public OpenSearchConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavPdlProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<SearchResponse> search(SearchRequest request) {
        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token ->
                        new OpenSearchCommand(webClient, request.getQuery().indices()[0],
                                token.getTokenValue(), request.getQuery().source().toString()).call())
                .map(response -> {
                    response.setRequest(request.getRequest());
                    return response;
                });
    }
}