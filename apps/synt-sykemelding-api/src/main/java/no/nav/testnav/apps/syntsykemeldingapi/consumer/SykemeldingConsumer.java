package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.TestnorgeSykemeldingProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.PostSykemeldingCommand;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class SykemeldingConsumer {

    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public SykemeldingConsumer(
            TestnorgeSykemeldingProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void opprettSykemelding(Sykemelding sykemelding) {
        tokenExchange.exchange(serviceProperties).flatMap(accessToken ->
                        new PostSykemeldingCommand(webClient, accessToken.getTokenValue(), sykemelding).call())
                .block();
    }
}
