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

@Slf4j
@Configuration
@Profile({"dev"})
@RequiredArgsConstructor
@VaultPropertySource(value = "serviceuser/dev/srvtps-forvalteren", propertyNamePrefix = "credentials.", ignoreSecretNotFound = false)
public class VaultConfig extends AbstractVaultConfiguration {

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        var token = System.getProperty("spring.cloud.vault.token");
        if (token == null) {
            throw new IllegalArgumentException("Påkrevet property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}