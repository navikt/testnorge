package no.nav.testnav.libs.securitycore.domain.azuread;

public class AzureTrygdeetatenClientCredential extends ClientCredential {

    AzureTrygdeetatenClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(tokenEndpoint, clientId, clientSecret);
    }

}
