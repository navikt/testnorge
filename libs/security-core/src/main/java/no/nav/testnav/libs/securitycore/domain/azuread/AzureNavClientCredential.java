package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class AzureNavClientCredential extends ClientCredential {

    private final String tokenEndpoint;

    AzureNavClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }

}
