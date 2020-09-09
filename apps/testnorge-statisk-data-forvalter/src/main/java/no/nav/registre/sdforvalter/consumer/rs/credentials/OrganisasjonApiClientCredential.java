package no.nav.registre.sdforvalter.consumer.rs.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class OrganisasjonApiClientCredential extends ClientCredential {
    public OrganisasjonApiClientCredential(
            @Value("${organsisasjon.api.client_id}") String clientId,
            @Value("${organsisasjon.api.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
