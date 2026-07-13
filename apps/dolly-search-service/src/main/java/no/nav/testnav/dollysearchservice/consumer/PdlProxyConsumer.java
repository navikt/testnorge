package no.nav.testnav.dollysearchservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.config.Consumers;
import no.nav.testnav.dollysearchservice.consumer.command.TagsGetCommand;
import no.nav.testnav.dollysearchservice.consumer.command.TagsPostCommand;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PdlProxyConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public PdlProxyConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            JsonMapper jsonMapper,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavPdlProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(jsonMapper))
                .build();
        this.tokenExchange = tokenExchange;
    }

    private static ExchangeStrategies getJacksonStrategy(JsonMapper jsonMapper) {
        return ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                            config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                            config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                        })
                .build();
    }

    public Mono<Map<String, List<String>>> getTags(List<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new TagsGetCommand(webClient, identer, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet tags for {} personer", response.size()));
    }

    public Mono<TagsOpprettingResponse> setTags(List<String> identer) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new TagsPostCommand(webClient, identer, token.getTokenValue()).call());
    }
}