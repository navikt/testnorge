package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureNavClientCredential extends ClientCredential {

    public AzureNavClientCredential(
            @Value("${AZURE_APP_CLIENT_ID:#{null}}") String clientId,
            @Value("${AZURE_APP_CLIENT_SECRET:#{null}}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }

}
