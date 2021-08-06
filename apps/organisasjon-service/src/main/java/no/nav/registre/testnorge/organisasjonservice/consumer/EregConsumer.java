package no.nav.registre.testnorge.organisasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.organisasjonservice.config.credentials.EregServiceProperties;
import no.nav.registre.testnorge.organisasjonservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjonservice.domain.Organisasjon;


@Slf4j
@Component
public class EregConsumer {
    private final WebClient webClient;
    private final EregServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;


    public EregConsumer(
            EregServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.serviceProperties = serviceProperties;
        this.accessTokenService = accessTokenService;
    }

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        var accessToken = accessTokenService.generateToken(serviceProperties).block();
        OrganisasjonDTO dto = new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), miljo, orgnummer).call();
        return dto != null ? new Organisasjon(dto) : null;
    }
}
