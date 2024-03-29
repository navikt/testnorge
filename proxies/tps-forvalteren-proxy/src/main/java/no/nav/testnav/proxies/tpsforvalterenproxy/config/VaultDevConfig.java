package no.nav.testnav.proxies.tpsforvalterenproxy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Configuration
@Profile({ "dev" })
@RequiredArgsConstructor
@VaultPropertySource(value = "serviceuser/dev/srvtps-forvalteren", propertyNamePrefix = "credentials.", ignoreSecretNotFound = false)
@VaultPropertySource(value = "kv/preprod/fss/tps-forvalteren-proxy/local", ignoreSecretNotFound = false)
public class VaultDevConfig extends AbstractVaultConfiguration {

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