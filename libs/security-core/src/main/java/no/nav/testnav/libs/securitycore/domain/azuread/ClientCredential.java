package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

@EqualsAndHashCode
@Getter
public class ClientCredential {

    private final String clientId;
    private final String clientSecret;

    public ClientCredential(String clientId, String clientSecret) {
        Assert.notNull(clientId, "AZURE_NAV_APP_CLIENT_ID must be set");
        Assert.notNull(clientSecret, "AZURE_NAV_APP_CLIENT_SECRET must be set");

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public final String toString() {
        return "ClientCredential{" +
                "clientId=[HIDDEN]" +
                ", clientSecret=[HIDDEN]" +
                '}';
    }
}
