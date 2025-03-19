package no.nav.testnav.dollysearchservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.config.Consumers;
import no.nav.testnav.dollysearchservice.consumer.command.OpenSearchCommand;
import no.nav.testnav.dollysearchservice.consumer.command.TagsGetCommand;
import no.nav.testnav.dollysearchservice.consumer.command.TagsPostCommand;
import no.nav.testnav.dollysearchservice.dto.SearchRequest;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static no.nav.testnav.dollysearchservice.consumer.utils.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlProxyConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PdlProxyConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavPdlProxy();
        this.webClient = webClientBuilder
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

    public Mono<Map<String, List<String>>> getTags(List<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token-> new TagsGetCommand(webClient, identer, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet tags for {} personer", response.size()));
    }

    public Mono<TagsOpprettingResponse> setTags(List<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new TagsPostCommand(webClient, identer, token.getTokenValue()).call());
    }
}