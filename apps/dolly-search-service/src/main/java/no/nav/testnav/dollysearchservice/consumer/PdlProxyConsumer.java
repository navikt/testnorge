package no.nav.testnav.dollysearchservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.config.Consumers;
import no.nav.testnav.dollysearchservice.consumer.command.TagsGetCommand;
import no.nav.testnav.dollysearchservice.consumer.command.TagsPostCommand;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
        this.tokenExchange = tokenExchange;
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