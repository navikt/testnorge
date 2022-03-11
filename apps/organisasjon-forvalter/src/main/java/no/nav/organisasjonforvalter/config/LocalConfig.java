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

@Slf4j
@Configuration
@Profile("dev")
@Import({
        FlywayConfiguration.class,
        VaultHikariConfiguration.class
})
@RequiredArgsConstructor
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
@VaultPropertySource(value = "serviceuser/dev/srvtestnorge", propertyNamePrefix = "serviceuser.", ignoreSecretNotFound = false)
class LocalConfig extends AbstractVaultConfiguration {

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