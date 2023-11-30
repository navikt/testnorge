package no.nav.registre.testnorge.organisasjonfastedataservice.consumer;

import no.nav.registre.testnorge.organisasjonfastedataservice.config.Consumers;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.command.GetOrdreCommand;
import no.nav.testnav.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public OrganisasjonBestillingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange) {
        serverProperties = consumers.getOrganisasjonBestillingService();
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<ItemDTO> getOrdreStatus(String ordreId) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
        var command = new GetOrdreCommand(webClient, accessToken.getTokenValue(), ordreId);
        return command.call();
    }
}
