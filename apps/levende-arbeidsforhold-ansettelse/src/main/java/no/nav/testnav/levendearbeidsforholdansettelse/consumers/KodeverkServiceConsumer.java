package no.nav.testnav.levendearbeidsforholdansettelse.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.config.Consumers;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.command.kodeverk.KodeverkServiceCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KodeverkServiceConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final ObjectMapper objectMapper;

    public KodeverkServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
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
        objectMapper = new ObjectMapper();
    }

    public Mono<List<String>> hentKodeverk(String kodeverk) {

        return tokenExchange.exchange(serverProperties)
                .flatMap(token -> new KodeverkServiceCommand(webClient, token.getTokenValue(), kodeverk, objectMapper).call())
                .map(Map::keySet)
                .map(ArrayList::new);
    }
}