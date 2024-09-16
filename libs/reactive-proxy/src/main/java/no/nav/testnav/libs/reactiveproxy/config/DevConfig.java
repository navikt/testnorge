package no.nav.testnav.libs.reactiveproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import static io.micrometer.common.util.StringUtils.isBlank;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
public class DevConfig extends AbstractVaultConfiguration {

    private static final String VAULT_TOKEN = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(VAULT_TOKEN, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(VAULT_TOKEN);
        if (isBlank(token)) {
            throw new IllegalArgumentException("Påkrevet property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty(VAULT_TOKEN));
    }
}