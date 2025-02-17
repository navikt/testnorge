package no.nav.dolly.libs.vault;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Will set a system property {@link #SPRING_CLOUD_VAULT_TOKEN} based on contents of a file {@link #NAIS_SECRET_FILENAME} if the Spring profile is set to {@link #PROD}.
 * Will only work for applications in {@code dev-fss}.
 */
@Slf4j
public class VaultTokenApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String NAIS_CLUSTER_NAME = "NAIS_CLUSTER_NAME";
    private static final String NAIS_SECRET_FILENAME = "/var/run/secrets/nais.io/vault/vault_token";
    private static final String PROD = "prod";
    private static final String SPRING_CLOUD_VAULT_TOKEN = "spring.cloud.vault.token";
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    public VaultTokenApplicationContextInitializer() {

        if (!PROD.equals(System.getProperty(SPRING_PROFILES_ACTIVE))) {
            log.info("Skipping setting system property {} for Spring profile != {}", SPRING_CLOUD_VAULT_TOKEN, PROD);
            return;
        }
        if ("dev-fss".equals(System.getProperty(NAIS_CLUSTER_NAME))) {
            throw new VaultException("Attempting to get Vault token in cluster %s which is not onprem".formatted(System.getProperty(NAIS_CLUSTER_NAME)));
        }
        System.setProperty(SPRING_CLOUD_VAULT_TOKEN, getVaultTokenFromFile());

    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context)
            throws VaultException {
        if (!PROD.equals(System.getProperty(SPRING_PROFILES_ACTIVE))) {
            log.info("Spring profile != {}, skipping validation", PROD);
            return;
        }
        if (System.getProperty(SPRING_CLOUD_VAULT_TOKEN, "").isEmpty()) {
            throw new VaultException("System property %s is NOT set".formatted(SPRING_CLOUD_VAULT_TOKEN));
        }
    }

    private static String getVaultTokenFromFile() {
        var path = Paths.get(NAIS_SECRET_FILENAME);
        log.info("Getting Vault token from file {}", path);
        try {
            return Files.readString(path).trim();
        } catch (IOException e) {
            throw new VaultException("Unable to read vault token from file %s".formatted(path.toString()), e);
        }
    }

}
