package no.nav.registre.varslingerservice.config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import static io.micrometer.common.util.StringUtils.isBlank;

@Configuration
@Profile("local")
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
@NonNullApi
public class LocalVaultConfig extends AbstractVaultConfiguration {

    private static final String TOKEN_SYSTEM_PROPERTY = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(TOKEN_SYSTEM_PROPERTY, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(TOKEN_SYSTEM_PROPERTY);
        if (isBlank(token)) {
            throw new IllegalArgumentException("Påkrevet property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty(TOKEN_SYSTEM_PROPERTY));
    }
}