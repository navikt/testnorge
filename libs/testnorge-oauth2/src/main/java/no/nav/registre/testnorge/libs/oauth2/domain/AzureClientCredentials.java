package no.nav.registre.testnorge.libs.oauth2.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureClientCredentials {
    private final String clientId;
    private final String clientSecret;

    public AzureClientCredentials(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${CLIENT_SECRET:#{null}}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String toString() {
        return "AzureClientCredentials{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='[hidden]" + '\'' +
                '}';
    }
}
