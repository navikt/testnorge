package no.nav.testnav.libs.reactivesecurity.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureTrygdeetatenClientCredential extends ClientCredential {
    private final String tokenEndpoint;

    public AzureTrygdeetatenClientCredential(
            @Value("${AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT:#{null}}") String tokenEndpoint,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_ID:#{null}}") String clientId,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_SECRET:#{null}}") String clientSecret
    ) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }
}
