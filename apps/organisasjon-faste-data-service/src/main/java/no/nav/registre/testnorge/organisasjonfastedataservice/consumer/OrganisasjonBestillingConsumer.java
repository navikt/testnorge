package no.nav.registre.testnorge.organisasjonfastedataservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.testnorge.organisasjonfastedataservice.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.command.GetOrdreCommand;
import no.nav.testnav.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final OrganisasjonBestillingServiceProperties serverProperties;
    private final TokenExchange tokenExchange;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties serverProperties,
            TokenExchange tokenExchange
    ) {
        this.serverProperties = serverProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<ItemDTO> getOrdreStatus(String ordreId) {
        var accessToken = tokenExchange.generateToken(serverProperties).block();
        var command = new GetOrdreCommand(webClient, accessToken.getTokenValue(), ordreId);
        return command.call();
    }
}
