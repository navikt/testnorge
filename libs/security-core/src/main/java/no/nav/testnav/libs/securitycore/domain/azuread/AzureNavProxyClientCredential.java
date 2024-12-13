package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AzureNavProxyClientCredential extends ClientCredential {

    private final String tokenEndpoint;

    AzureNavProxyClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }

}
