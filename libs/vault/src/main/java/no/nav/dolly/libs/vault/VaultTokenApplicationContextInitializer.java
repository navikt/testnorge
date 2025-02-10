package no.nav.dolly.libs.vault;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Will set a system property {@code VAULT_TOKEN} if the Spring profile is set to {@link #PROFILE}.
 * Will only work for applications in {@code dev-fss}.
 */
public class VaultTokenApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROFILE = "prod";

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context) {

        Stream
                .of(context.getEnvironment().getActiveProfiles())
                .filter(profile -> profile.equals(PROFILE))
                .findAny()
                .ifPresent(profile -> System.setProperty(VAULT_TOKEN_SYSTEM_PROPERTY, getVaultToken()));

    }

    private static final String NAIS_CLUSTER_SYSTEM_PROPERTY = "NAIS_CLUSTER_NAME";
    private static final String VAULT_TOKEN_SYSTEM_PROPERTY = "spring.cloud.vault.token";
    private static final String VAULT_TOKEN_ENVIRONMENT_PROPERTY = "VAULT_TOKEN";

    private static String getVaultToken() {

        Optional
                .ofNullable(System.getProperty(NAIS_CLUSTER_SYSTEM_PROPERTY))
                .filter(cluster -> !"dev-fss".equals(cluster))
                .ifPresent(cluster -> {
                    throw new VaultException("Attempting to get Vault token in cluster %s which is not onprem".formatted(cluster));
                });

        if (System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY) != null) {
            return System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY);
        }

        var environment = new AnnotationConfigApplicationContext().getEnvironment();
        if (environment.containsProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY) && !"".equals(environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY))) {
            return environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY);
        }

        var path = Paths.get("/var/run/secrets/nais.io/vault/vault_token");
        if (!Files.exists(path)) {
            throw new VaultException("Could not get vault token from nonexisting file %s".formatted(path.toString()));
        }
        try {
            return Files.readString(path).trim();
        } catch (IOException e) {
            throw new VaultException("Unable to read vault token from existing file %s".formatted(path.toString()), e);
        }

    }

}
