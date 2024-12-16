package no.nav.testnav.libs.reactivesecurity.domain;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

import no.nav.testnav.libs.securitycore.domain.azuread.ClientCredential;

@Configuration
@Getter
public class AzureTrygdeetatenClientCredential extends ClientCredential {

    private final String tokenEndpoint;

    public AzureTrygdeetatenClientCredential(
            @Value("${AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT:#{null}}") String tokenEndpoint,
            @Value("#{systemProperties['spring.profiles.active'] == 'test' ? 'test-client-id' : '${AZURE_TRYGDEETATEN_APP_CLIENT_ID:#{null}}'}") String clientId,
            @Value("#{systemProperties['spring.profiles.active'] == 'test' ? 'test-client-secret' : '${AZURE_TRYGDEETATEN_CLIENT_SECRET:#{null}}'}") String clientSecret
    ) {
        super(clientId, clientSecret);
        this.tokenEndpoint = tokenEndpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AzureTrygdeetatenClientCredential that = (AzureTrygdeetatenClientCredential) o;
        return Objects.equals(tokenEndpoint, that.tokenEndpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tokenEndpoint);
    }

}
