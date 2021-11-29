package no.nav.testnav.libs.reactivesecurity.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConditionalOnProperty("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT")
public class TrygdeetatenConfig {
    private final String tokenEndpoint;
    private final String clientId;
    private final String clientSecret;

    public TrygdeetatenConfig(
            @Value("${AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT}") String tokenEndpoint,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_ID}") String clientId,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_SECRET}") String clientSecret
    ) {
        this.tokenEndpoint = tokenEndpoint;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
