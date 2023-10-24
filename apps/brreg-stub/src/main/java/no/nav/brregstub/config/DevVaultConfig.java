package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import static net.logstash.logback.util.StringUtils.isBlank;

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
        var token = System.getProperty("spring.cloud.vault.token");
        if (isBlank(token)) {
            throw new IllegalArgumentException("Påkrevet property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}