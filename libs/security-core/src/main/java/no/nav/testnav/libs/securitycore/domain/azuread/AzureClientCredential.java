package no.nav.testnav.libs.securitycore.domain.azuread;

public class AzureClientCredential extends ClientCredential {

    AzureClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(tokenEndpoint, clientId, clientSecret);
    }

}