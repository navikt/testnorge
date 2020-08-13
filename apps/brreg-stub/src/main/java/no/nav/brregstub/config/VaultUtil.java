package no.nav.brregstub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public final class VaultUtil {

    private static final String VAULT_TOKEN_PROPERTY = "VAULT_TOKEN";
    private static final String NAIS_VAULT_TOKEN_PATH = "/var/run/secrets/nais.io/vault/vault_token";

    private static String getVaultToken() {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();

        if (environment.containsProperty(VAULT_TOKEN_PROPERTY)) {
            return environment.getProperty(VAULT_TOKEN_PROPERTY);
        }

        try {
            var encoded = Files.readAllBytes(Paths.get(NAIS_VAULT_TOKEN_PATH));
            return new String(encoded, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not get vault token from %s", NAIS_VAULT_TOKEN_PATH), e);
        }
    }

    public static void setCloudVaultToken() {
        System.setProperty("spring.cloud.vault.token", getVaultToken());
    }
}
