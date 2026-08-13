package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.kodeverk.KodeverkServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KodeverkServiceConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final JsonMapper jsonMapper;

    public KodeverkServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient,
            JsonMapper jsonMapper
    ) {
        serverProperties = consumers.getTestnavKodeverkService();
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(8 * 1024 * 1024))
                .build();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(exchangeStrategies)
                .build();
        this.tokenExchange = tokenExchange;
        this.jsonMapper = jsonMapper;
    }

    public Mono<List<String>> hentKodeverk(String kodeverk) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new KodeverkServiceCommand(webClient, token.getTokenValue(), kodeverk, jsonMapper).call())
                .map(Map::keySet)
                .map(ArrayList::new);
    }
}