package no.nav.registre.testnorge.avhengighetsanalysefrontend.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class AvhengighetsanalyseFrontendClientCredentials extends ClientCredential {
    public AvhengighetsanalyseFrontendClientCredentials(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${CLIENT_SECRET}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
