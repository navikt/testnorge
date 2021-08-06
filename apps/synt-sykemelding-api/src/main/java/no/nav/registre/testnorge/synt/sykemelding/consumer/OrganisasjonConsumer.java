package no.nav.registre.testnorge.synt.sykemelding.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.commands.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import no.nav.registre.testnorge.synt.sykemelding.config.credentials.OrganisasjonServiceProperties;

@Component
public class OrganisasjonConsumer {
    private final OrganisasjonServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;
    private final WebClient webClient;

    public OrganisasjonConsumer(
            OrganisasjonServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.serviceProperties = serviceProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }


    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties).block();
        return new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, "q1").call();
    }
}