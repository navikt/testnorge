package no.nav.testnav.proxies.arbeidsplassencvproxy.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.proxies.arbeidsplassencvproxy.config.FakedingsProperties;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.command.FakedingsGetCommand;
import no.nav.testnav.proxies.arbeidsplassencvproxy.util.JacksonExchangeStrategyUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FakedingsConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public FakedingsConsumer(TokenExchange tokenExchange, FakedingsProperties fakedingsProperties, ObjectMapper objectMapper) {

        this.webClient = WebClient.builder()
                .baseUrl(fakedingsProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
        this.serverProperties = fakedingsProperties;
        this.tokenExchange = tokenExchange;
    }

    public Mono<String> getFakeToken(String ident, String token) {

        return new FakedingsGetCommand(webClient, ident, token).call();
    }
}
