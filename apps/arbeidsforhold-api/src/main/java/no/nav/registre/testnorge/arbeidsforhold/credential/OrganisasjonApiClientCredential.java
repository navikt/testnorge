package no.nav.registre.testnorge.arbeidsforhold.credential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class OrganisasjonApiClientCredential extends ClientCredential {
    public OrganisasjonApiClientCredential(
            @Value("${consumers.organisasjonapi.client_id}") String clientId,
            @Value("${consumers.organisasjonapi.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
