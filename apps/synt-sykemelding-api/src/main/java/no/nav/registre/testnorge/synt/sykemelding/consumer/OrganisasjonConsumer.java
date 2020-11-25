package no.nav.registre.testnorge.synt.sykemelding.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class OrganisasjonConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final String clientId;

    public OrganisasjonConsumer(
            @Value("${consumers.organisasjonapi.url}") String url,
            @Value("${consumers.organisasjonapi.client_id}") String clientId,
            AccessTokenService accessTokenService
    ) {
        this.clientId = clientId;
        this.accessTokenService = accessTokenService;

        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        AccessToken accessToken = accessTokenService.generateToken(clientId);
        return new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, "q1").call();
    }
}