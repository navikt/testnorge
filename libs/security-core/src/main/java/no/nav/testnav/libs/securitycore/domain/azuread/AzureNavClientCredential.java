package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureNavClientCredential extends ClientCredential {

    public AzureNavClientCredential(
            @Value("${spring.security.oauth2.client.registration.aad.client-id:#{null}}") String clientId,
            @Value("${spring.security.oauth2.client.registration.aad.client-secret:#{null}}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }

}
