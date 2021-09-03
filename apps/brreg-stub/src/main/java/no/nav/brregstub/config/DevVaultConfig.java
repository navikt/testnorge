package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;

@Configuration
@Profile("local")
@VaultPropertySource(value = "kv/preprod/fss/brreg-stub/local", ignoreSecretNotFound = false)
public class DevVaultConfig  extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}