package no.nav.registre.testnorge.organisasjonfastedataservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.testnav.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonfastedataservice.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.command.GetOrdreCommand;

@Component
public class OrganisasjonBestillingConsumer {
    private final WebClient webClient;
    private final OrganisasjonBestillingServiceProperties serverProperties;
    private final AccessTokenService accessTokenService;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties serverProperties,
            AccessTokenService accessTokenService
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public List<ItemDTO> getOrdreStatus(String ordreId) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        var command = new GetOrdreCommand(webClient, accessToken.getTokenValue(), ordreId);
        return command.call();
    }
}
