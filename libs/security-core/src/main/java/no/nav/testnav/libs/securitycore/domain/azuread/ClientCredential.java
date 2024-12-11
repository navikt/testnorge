package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.util.Assert;

@EqualsAndHashCode
@Getter
public class ClientCredential {

    private static final String MISSING_CONFIG_MESSAGE = "AZURE_NAV_APP_CLIENT_ID and AZURE_NAV_APP_CLIENT_SECRET must be set";

    private final String clientId;
    private final String clientSecret;

    public ClientCredential(String clientId, String clientSecret) {
        Assert.noNullElements(new String[]{clientId, clientSecret}, MISSING_CONFIG_MESSAGE);

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
