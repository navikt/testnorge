package no.nav.registre.testnorge.organisasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.organisasjonservice.config.credentials.EregServiceProperties;
import no.nav.registre.testnorge.organisasjonservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjonservice.domain.Organisasjon;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;


@Slf4j
@Component
public class EregConsumer {
    private final WebClient webClient;
    private final EregServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;


    public EregConsumer(
            EregServiceProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
    }

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        var accessToken = tokenExchange.generateToken(serviceProperties).block();
        OrganisasjonDTO dto = new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), miljo, orgnummer).call();
        return dto != null ? new Organisasjon(dto) : null;
    }
}
