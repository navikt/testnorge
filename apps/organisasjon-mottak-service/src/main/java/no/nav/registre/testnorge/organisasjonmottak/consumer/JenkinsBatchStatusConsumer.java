package no.nav.registre.testnorge.organisasjonmottak.consumer;

import no.nav.registre.testnorge.organisasjonmottak.config.Consumers;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.RegisterEregBestillingCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JenkinsBatchStatusConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public JenkinsBatchStatusConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getJenkinsBatchStatusService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public void registerBestilling(String uuid, String miljo, Long itemId) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        new RegisterEregBestillingCommand(webClient, accessToken.getTokenValue(), uuid, miljo, itemId).run();
    }
}
