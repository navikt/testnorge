package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import no.nav.testnav.apps.syntsykemeldingapi.config.Consumers;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrganisasjonConsumer {
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    private static final String MILJOE = "q1";

    public OrganisasjonConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavOrganisasjonService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        return tokenExchange.exchange(serverProperties).flatMap(accessToken ->
                        new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, MILJOE).call())
                .block();
    }
}