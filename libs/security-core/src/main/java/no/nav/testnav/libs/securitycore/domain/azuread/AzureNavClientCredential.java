package no.nav.testnav.libs.securitycore.domain.azuread;

public class AzureNavClientCredential extends ClientCredential {

    AzureNavClientCredential(String clientId, String clientSecret) {
        super(clientId, clientSecret);
    }

}