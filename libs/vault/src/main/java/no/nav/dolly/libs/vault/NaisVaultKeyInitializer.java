package no.nav.dolly.libs.vault;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Set the Vault token system property from a file mounted by NAIS.
 * <br/>
 * {@code ApplicationContextInitializer} or {@code EnvironmentPostProcessor} cannot be used,
 * as they are invoked <em>after</em> the Vault configuration has been loaded.
 */
@Slf4j
public class NaisVaultKeyInitializer {

    private static final String PROPERTY = "spring.cloud.vault.token";
    private static final String PATH = "/var/run/secrets/nais.io/vault/vault_token";

    public static void run()
        throws IllegalStateException {

        try {
            var path = Paths.get(PATH);
            if (Files.exists(path)) {
                var value = Files.readString(path).trim();
                System.setProperty(PROPERTY, value);
                log.info("Vault token set from file {}", PATH);
            } else {
                log.warn("File not found at {}; hopefully you're running locally", PATH);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error setting Vault token from file %s".formatted(PATH), e);
        }

    }
}
