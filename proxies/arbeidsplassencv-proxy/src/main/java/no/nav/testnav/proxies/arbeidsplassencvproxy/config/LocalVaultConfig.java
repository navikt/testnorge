package no.nav.testnav.proxies.arbeidsplassencvproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("dev")
@VaultPropertySource(
        value = "secret/dolly/lokal",
        ignoreSecretNotFound = false
)
public class LocalVaultConfig extends AbstractVaultConfiguration {

    @Override
    @NonNull
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    @NonNull
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}
