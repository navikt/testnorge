package no.nav.dolly.libs.nais;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class NaisVaultKeyInitializer {

    private static final String KEY = "SPRING_CLOUD_VAULT_TOKEN";
    private static final String PROPERTY = "spring.cloud.vault.token";
    private static final String PATH = "/var/run/secrets/nais.io/vault/vault_token";

    public static void run() {

        try {
            var path = Paths.get(PATH);
            if (Files.exists(path)) {
                var value = Files.readString(path).trim();
                System.setProperty(KEY, value);
                System.setProperty(PROPERTY, value);
                var maskedValue = value.charAt(0) + "*".repeat(value.length() - 2) + value.charAt(value.length() - 1);
                log.info("System property {} set from file {} with value {}", KEY, PATH, maskedValue);
            } else {
                log.warn("File not found at {}; hopefully you're running locally", PATH);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting system property %s from file %s".formatted(KEY, PATH), e);
        }

    }
}
