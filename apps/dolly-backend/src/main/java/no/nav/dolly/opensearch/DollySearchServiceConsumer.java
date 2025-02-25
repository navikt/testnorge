package no.nav.dolly.opensearch;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.opensearch.command.DollySearchServicePostCommand;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DollySearchServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public DollySearchServiceConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient.Builder webClientBuilder) {

        this.tokenService = tokenService;
        serverProperties = consumers.getDollySearchService();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<SearchResponse> doPersonSearch(SearchRequest request) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new DollySearchServicePostCommand(webClient, request, token.getTokenValue()).call());
    }
}
