package no.nav.registre.testnorge.organisasjonmottak.consumer;

import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsBatchStatusServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JenkinsBatchStatusConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public JenkinsBatchStatusConsumer(
            JenkinsBatchStatusServiceProperties properties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = tokenExchange.exchange(properties).block();
        new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).run();
    }
}
