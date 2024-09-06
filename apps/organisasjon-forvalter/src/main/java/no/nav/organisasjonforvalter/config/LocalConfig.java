package no.nav.organisasjonforvalter.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Configuration
@Profile("local")
@Import({
        FlywayConfiguration.class,
        VaultHikariConfiguration.class
})
@RequiredArgsConstructor
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
class LocalConfig extends AbstractVaultConfiguration {

    private static final String VAULT_PROPERTY = "spring.cloud.vault.token";
    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(VAULT_PROPERTY, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(VAULT_PROPERTY);
        if (isBlank(token)) {
            throw new IllegalArgumentException(String.format("Påkrevet property '%s' er ikke satt.", VAULT_PROPERTY));
        }
        return new TokenAuthentication(System.getProperty(VAULT_PROPERTY));
    }
}