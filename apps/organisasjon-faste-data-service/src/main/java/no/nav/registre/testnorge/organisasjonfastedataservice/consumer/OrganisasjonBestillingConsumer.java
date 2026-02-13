package no.nav.registre.testnorge.organisasjonfastedataservice.consumer;

import no.nav.registre.testnorge.organisasjonfastedataservice.config.Consumers;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.command.GetOrdreCommand;
import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class OrganisasjonBestillingConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public OrganisasjonBestillingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getOrganisasjonBestillingService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<List<ItemDTO>> getOrdreStatus(String ordreId) {
        return tokenExchange.exchange(serverProperties)
                .flatMap(accessToken -> new GetOrdreCommand(webClient, accessToken.getTokenValue(), ordreId).call());
    }
}
