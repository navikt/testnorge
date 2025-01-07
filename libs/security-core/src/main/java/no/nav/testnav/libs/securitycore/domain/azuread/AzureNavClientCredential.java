package no.nav.testnav.libs.securitycore.domain.azuread;

public class AzureNavClientCredential extends ClientCredential {

    AzureNavClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(tokenEndpoint, clientId, clientSecret);
    }

}
