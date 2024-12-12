package no.nav.testnav.libs.securitycore.domain.azuread;

import lombok.Getter;

import java.util.Objects;

@Getter
public class AzureTrygdeetatenClientCredential extends ClientCredential {

    private final String tokenEndpoint;

    public AzureTrygdeetatenClientCredential(String tokenEndpoint, String clientId, String clientSecret) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(tokenEndpoint, ((AzureTrygdeetatenClientCredential) o).getTokenEndpoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokenEndpoint);
    }

}
