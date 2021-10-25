package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Profile("dev")
@Configuration
@VaultPropertySource(value = "kv/preprod/fss/testnav-skjermingsregister-proxy/dev", ignoreSecretNotFound = false)
public class DevVaultConfig extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}