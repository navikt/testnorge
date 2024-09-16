package no.nav.dolly.budpro;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.util.Optional;

@Configuration
@Profile("local")
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
@NonNullApi
public class LocalVaultConfig extends AbstractVaultConfiguration {

    private static final String PROPERTY = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        Optional
                .ofNullable(System.getenv().get("VAULT_TOKEN"))
                .ifPresent(token -> System.setProperty(PROPERTY, token));
        var token = Optional
                .ofNullable(System.getProperty(PROPERTY))
                .orElseThrow(() -> new IllegalArgumentException("Required system property '" + PROPERTY + "' not set; cannot get secrets from Vault"));
        return new TokenAuthentication(token);
    }

}
