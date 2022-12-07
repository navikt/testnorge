package no.nav.registre.testnorge.helsepersonellservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonellservice.config.credentials.SamhandlerregisteretServerProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetSamhandlerCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static java.util.Objects.nonNull;
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

    public Samhandler getSamhandler(String ident) {
        var response = tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetSamhandlerCommand(ident, webClient, accessToken.getTokenValue()).call())
                .block();

        if (nonNull(response)) {
            return response.stream()
                    .filter(Objects::nonNull)
                    .map(Samhandler::new)
                    .findFirst()
                    .orElse(null);
        } else {
            return null;
        }
    }
}

