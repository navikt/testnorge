package no.nav.registre.frikort.config.dev;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "kv/preprod/fss/testnorge-frikort/local", ignoreSecretNotFound = false)
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
public class VaultConfig  extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        var token = System.getProperty("spring.cloud.vault.token");
        if (token == null) {
            throw new IllegalArgumentException("Påkrevd property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}