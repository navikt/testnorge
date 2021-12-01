package no.nav.registre.testnorge.organisasjonmottak.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.organisasjonmottak.config.properties.JenkinsBatchStatusServiceProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class JenkinsBatchStatusConsumer {
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public JenkinsBatchStatusConsumer(
            JenkinsBatchStatusServiceProperties properties,
            TokenExchange tokenExchange
    ) {
        this.properties = properties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = tokenExchange.exchange(properties).block();
        new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).run();
    }
}
