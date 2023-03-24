package no.nav.testnav.proxies.skjermingsregisterproxy;

import io.micrometer.common.lang.NonNullApi;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Profile("dev")
@Import(DevConfig.class)
@Configuration
@VaultPropertySource(value = "kv/preprod/fss/testnav-skjermingsregister-proxy/dev", ignoreSecretNotFound = false)
@NonNullApi
public class DevVaultConfig extends AbstractVaultConfiguration {

    private static final String PROPERTY_NAME = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(PROPERTY_NAME, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(PROPERTY_NAME);
        if (token == null) {
            throw new IllegalArgumentException("Påkreved property '%s' er ikke satt.".formatted(PROPERTY_NAME));
        }
        return new TokenAuthentication(System.getProperty(PROPERTY_NAME));
    }

}