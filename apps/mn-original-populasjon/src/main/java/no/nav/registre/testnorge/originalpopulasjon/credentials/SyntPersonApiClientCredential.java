package no.nav.registre.testnorge.originalpopulasjon.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class SyntPersonApiClientCredential extends ClientCredential {
    public SyntPersonApiClientCredential(
            @Value("${consumer.synt-person-api.client_id}") String clientId,
            @Value("${consumer.synt-person-api.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
