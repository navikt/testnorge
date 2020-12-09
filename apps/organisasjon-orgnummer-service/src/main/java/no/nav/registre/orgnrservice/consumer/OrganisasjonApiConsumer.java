package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;

@Slf4j
@Component
public class OrganisasjonApiConsumer {

    private WebClient webClient;
    private final String clientId;
    private final AzureClientCredentials clientCredential;
    private final ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService;

    OrganisasjonApiConsumer (@Value("${consumers.organisasjon-api.url}") String url,
                             @Value("${consumers.organisasjon-api.client_id}") String clientId,
                             AzureClientCredentials clientCredential,
                             ClientCredentialGenerateAccessTokenService clientCredentialGenerateAccessTokenService) {
        this.clientId = clientId;
        this.clientCredential = clientCredential;
        this.clientCredentialGenerateAccessTokenService = clientCredentialGenerateAccessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(url)
                .build();
    }

    OrganisasjonDTO getOrgnrFraMiljoe (String orgnummer, String miljoe, String token) {
        try { return new GetOrganisasjonCommand(webClient, orgnummer, miljoe, token).call();
        } catch( Exception e) {
            throw new RuntimeException("Kunne ikke hente organisasjon " + orgnummer + " fra miljoe " + miljoe);
        }
    }

    public OrganisasjonDTO getOrgnr (String orgnummer) {
        //evt loop gjennom miljø
        String token = clientCredentialGenerateAccessTokenService
                .generateToken(clientCredential, clientId)
                .getTokenValue();
        log.info("Om token starter med de rette to bokstavene {}", token.startsWith("ey"));
        return getOrgnrFraMiljoe(orgnummer, "q1", token);
    }
}
