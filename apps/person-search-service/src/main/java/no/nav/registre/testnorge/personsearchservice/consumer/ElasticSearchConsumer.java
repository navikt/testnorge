package no.nav.registre.testnorge.personsearchservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.personsearchservice.config.Consumers;
import no.nav.registre.testnorge.personsearchservice.consumer.command.ElasticSearchCommand;
import no.nav.registre.testnorge.personsearchservice.model.Response;
import no.nav.registre.testnorge.personsearchservice.model.SearchResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.opensearch.action.search.SearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static no.nav.registre.testnorge.personsearchservice.service.utils.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class ElasticSearchConsumer {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public ElasticSearchConsumer(
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
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    private Flux<Object> getSearchResponse(SearchRequest searchRequest) {
        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token ->
                        new ElasticSearchCommand(webClient, searchRequest.indices()[0], token.getTokenValue(), searchRequest.source().toString()).call())
                .map(SearchResponse::getHits)
                .map(SearchResponse.SearchHits::getHits)
                .flatMap(Flux::fromIterable)
                .map(SearchResponse.SearchHit::get_source);
    }

    @SneakyThrows
    public Flux<Response> search(SearchRequest searchRequest) {
        return getSearchResponse(searchRequest)
                .map(response -> objectMapper.convertValue(response, Response.class));
    }

    @SneakyThrows
    public Flux<String> searchWithJsonResponse(SearchRequest searchRequest) {
        return getSearchResponse(searchRequest)
                .map(response -> {
                    try {
                        return objectMapper.writeValueAsString(response);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}