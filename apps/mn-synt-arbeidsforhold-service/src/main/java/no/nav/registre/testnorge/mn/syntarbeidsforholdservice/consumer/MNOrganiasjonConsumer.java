package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.common.command.GetMNOrganisasjonerCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials.MNOrganisasjonApiClientProperties;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Component
public class MNOrganiasjonConsumer {

    private final WebClient webClient;
    private final MNOrganisasjonApiClientProperties mnOrganisasjonApiClientProperties;
    private final ClientCredentialGenerateAccessTokenService accessTokenService;

    public MNOrganiasjonConsumer(
            MNOrganisasjonApiClientProperties mnOrganisasjonApiClientProperties,
            ClientCredentialGenerateAccessTokenService accessTokenService
    ) {
        this.mnOrganisasjonApiClientProperties = mnOrganisasjonApiClientProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient
                .builder()
                .baseUrl(mnOrganisasjonApiClientProperties.getBaseUrl())
                .build();
    }

    public List<Organisajon> getOrganisajoner(String miljo) {
        AccessToken accessToken = accessTokenService.generateToken(mnOrganisasjonApiClientProperties);
        List<OrganisasjonDTO> list = new GetMNOrganisasjonerCommand(webClient, accessToken.getTokenValue(), miljo).call();
        return list.stream().map(Organisajon::new).collect(Collectors.toList());
    }
}