package no.nav.registre.testnorge.helsepersonellservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.SamhandlerregisteretServerProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static no.nav.registre.testnorge.helsepersonellservice.util.ExhangeStrategyUtil.biggerMemorySizeExchangeStrategy;

@Slf4j
@Component
public class SamhandlerregisteretConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final SamhandlerregisteretServerProperties serverProperties;

    public SamhandlerregisteretConsumer(
            TokenExchange tokenExchange,
            SamhandlerregisteretServerProperties serverProperties,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(biggerMemorySizeExchangeStrategy())
                .baseUrl(serverProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<AccessToken> getToken() {
        return tokenExchange.exchange(serverProperties);
    }

    public Mono<Samhandler> getSamhandler(String ident, AccessToken accessToken) {
        return new GetSamhandlerCommand(ident, webClient, accessToken.getTokenValue()).call()
                .filter(Objects::nonNull)
                .map(Samhandler::new)
                .next();
    }
}

