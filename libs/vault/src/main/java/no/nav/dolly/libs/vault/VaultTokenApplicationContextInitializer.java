package no.nav.dolly.libs.vault;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Will set a system property {@code VAULT_TOKEN} if the Spring profile is set to {@link #PROFILE}.
 * Will only work for applications in {@code dev-fss}.
 */
@Slf4j
public class VaultTokenApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String NAIS_CLUSTER_SYSTEM_PROPERTY = "NAIS_CLUSTER_NAME";
    private static final String PROFILE = "prod";
    private static final String VAULT_TOKEN_ENVIRONMENT_PROPERTY = "VAULT_TOKEN";
    private static final String VAULT_TOKEN_SYSTEM_PROPERTY = "spring.cloud.vault.token";

    public VaultTokenApplicationContextInitializer() {
        if (PROFILE.equals(System.getProperty("spring.profiles.active"))) {
            log.info("Setting system property {} for profile {}", VAULT_TOKEN_SYSTEM_PROPERTY, PROFILE);
            System.setProperty(VAULT_TOKEN_SYSTEM_PROPERTY, getVaultToken());
        }
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context)
        throws IllegalStateException {
        if (System.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY, "").isEmpty()) {
            throw new VaultException("System property %s is NOT set".formatted(VAULT_TOKEN_ENVIRONMENT_PROPERTY));
        }
    }

    private static String getVaultToken() {

        Optional
                .ofNullable(System.getProperty(NAIS_CLUSTER_SYSTEM_PROPERTY))
                .filter(cluster -> !"dev-fss".equals(cluster))
                .ifPresent(cluster -> {
                    throw new VaultException("Attempting to get Vault token in cluster %s which is not onprem".formatted(cluster));
                });

        if (System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY) != null) {
            log.info("Getting Vault token from system property {}", VAULT_TOKEN_ENVIRONMENT_PROPERTY);
            return System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY);
        }

        var environment = new AnnotationConfigApplicationContext().getEnvironment();
        if (!environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY, "").isEmpty()) {
            log.info("Getting Vault token from environment property {}", VAULT_TOKEN_ENVIRONMENT_PROPERTY);
            return environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY);
        }

        var path = Paths.get("/var/run/secrets/nais.io/vault/vault_token");
        try {
            log.info("Getting Vault token from file {}", path);
            return Files.readString(path).trim();
        } catch (IOException e) {
            throw new VaultException("Unable to read vault token from file %s".formatted(path.toString()), e);
        }

    }

}
