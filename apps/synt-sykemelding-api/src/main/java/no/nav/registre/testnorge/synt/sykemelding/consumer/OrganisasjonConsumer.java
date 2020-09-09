package no.nav.registre.testnorge.synt.sykemelding.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.synt.sykemelding.consumer.credential.OrganisasjonApiClientCredential;

@Component
public class OrganisasjonConsumer {
    private final WebClient webClient;
    private final ClientCredential clientCredential;
    private final AccessTokenService accessTokenService;

    public OrganisasjonConsumer(
            @Value("${consumers.organisasjonapi.url}") String url,
            OrganisasjonApiClientCredential clientCredential,
            AccessTokenService accessTokenService
    ) {

        this.clientCredential = clientCredential;
        this.accessTokenService = accessTokenService;

        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();

    }

    public OrganisasjonDTO getOrganisasjon(String orgnummer) {
        AccessToken accessToken = accessTokenService.generateToken(clientCredential);
        return new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, "q1").call();
    }
}