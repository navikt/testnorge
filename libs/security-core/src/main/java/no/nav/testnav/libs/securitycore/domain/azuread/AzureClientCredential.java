package no.nav.testnav.libs.securitycore.domain.azuread;

public class AzureClientCredential extends ClientCredential {

    AzureClientCredential(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }

}