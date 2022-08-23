package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import no.nav.testnav.apps.syntsykemeldingapi.config.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OrganisasjonConsumer {
    private final OrganisasjonServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    public OrganisasjonConsumer(
            OrganisasjonServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        return tokenExchange.exchange(serviceProperties).flatMap(accessToken ->
                        new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, "q1").call())
                .block();
    }
}