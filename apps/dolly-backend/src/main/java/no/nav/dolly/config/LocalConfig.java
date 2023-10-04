package no.nav.dolly.config;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@Profile("local")
@Import({ FlywayConfiguration.class,
        VaultHikariConfiguration.class })
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
@RequiredArgsConstructor
public class LocalConfig extends AbstractVaultConfiguration {

    private static final String TOKEN = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(TOKEN, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(TOKEN);
        if (isBlank(token)) {
            throw new IllegalArgumentException(String.format("Påkrevd property '%s' er ikke satt.", TOKEN));
        }
        return new TokenAuthentication(System.getProperty(TOKEN));
    }
} 