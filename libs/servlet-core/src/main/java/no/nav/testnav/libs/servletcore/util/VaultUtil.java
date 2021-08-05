package no.nav.testnav.libs.servletcore.util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class VaultUtil {

    private VaultUtil() {
    }

    private static final String VAULT_TOKEN_PROPERTY = "VAULT_TOKEN";

    private static String getVaultToken() {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();

        try {
            if (environment.containsProperty(VAULT_TOKEN_PROPERTY) && !"".equals(environment.getProperty(VAULT_TOKEN_PROPERTY))) {
                return environment.getProperty(VAULT_TOKEN_PROPERTY);
            } else if (Files.exists(Paths.get("/var/run/secrets/nais.io/vault/vault_token"))) {
                byte[] encoded = Files.readAllBytes(Paths.get("/var/run/secrets/nais.io/vault/vault_token"));
                return new String(encoded, StandardCharsets.UTF_8).trim();
            } else {
                throw new RuntimeException("Klarer ikke å hente vault token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get a vault token for authentication", e);
        }
    }

    public static void initCloudVaultToken() {
        System.setProperty("spring.cloud.vault.token", getVaultToken());
    }
}
