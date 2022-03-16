package no.nav.testnav.libs.reactivesecurity.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;

@Getter
@EqualsAndHashCode(callSuper = false)
@Configuration
public class AzureNavProxyClientCredential extends ClientCredential {
    private final String tokenEndpoint;

    public AzureNavProxyClientCredential(
            @Value("${AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT:#{null}}") String tokenEndpoint,
            @Value("${AZURE_NAV_APP_CLIENT_ID:#{null}}") String clientId,
            @Value("${AZURE_NAV_APP_CLIENT_SECRET:#{null}}") String clientSecret
    ) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }
}
