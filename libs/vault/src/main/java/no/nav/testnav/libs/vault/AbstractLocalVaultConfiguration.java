package no.nav.testnav.libs.vault;

import io.micrometer.common.lang.NonNullApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
@VaultPropertySource(value = "secret/dolly/lokal", ignoreSecretNotFound = false)
@NonNullApi
@Slf4j
public abstract class AbstractLocalVaultConfiguration extends AbstractVaultConfiguration {

    private static final String SYSTEM_PROPERTY = "spring.cloud.vault.token";
    private static final String ENVIRONMENT_VARIABLE = "VAULT_TOKEN";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication()
        throws IllegalArgumentException {

        if (missingToken()) {
            setTokenFromEnvironment();
        }
        if (missingToken()) {
            setTokenFromCommand();
        }
        if (missingToken()) {
            throw new IllegalArgumentException("Vault token '%s' not configured as a system property".formatted(SYSTEM_PROPERTY));
        }
        return new TokenAuthentication(System.getProperty(SYSTEM_PROPERTY));

    }

    private static boolean missingToken() {
        var token = System.getProperty(SYSTEM_PROPERTY);
        return token == null || token.isEmpty();
    }

    private static void setTokenFromEnvironment() {
        if (System.getenv().containsKey(ENVIRONMENT_VARIABLE)) {
            System.setProperty(SYSTEM_PROPERTY, System.getenv(ENVIRONMENT_VARIABLE));
            log.info("Vault token '{}' set from environment", SYSTEM_PROPERTY);
        }
    }

    private static void setTokenFromCommand() {
        try {
            var process = new ProcessBuilder()
                    .command("vault", "print", "token")
                    .start();
            var token = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .readLine();
            if (token != null && !token.isEmpty()) {
                System.setProperty(SYSTEM_PROPERTY, token);
                log.info("Vault token '{}' set from command", SYSTEM_PROPERTY);
            }
        } catch (IOException e) {
            log.warn("Failed to read token from 'vault print token'", e);
        }
    }

}
