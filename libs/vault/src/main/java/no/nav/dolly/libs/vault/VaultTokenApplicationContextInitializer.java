package no.nav.dolly.libs.vault;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Will set a system property {@code VAULT_TOKEN} if the Spring profile is set to {@link #PROFILE}.
 * Will only work for applications in {@code dev-fss}.
 */
public class VaultTokenApplicationContextInitializer implements Ordered, ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String NAIS_CLUSTER_SYSTEM_PROPERTY = "NAIS_CLUSTER_NAME";
    private static final String PROFILE = "prod";
    private static final String VAULT_TOKEN_ENVIRONMENT_PROPERTY = "VAULT_TOKEN";
    private static final String VAULT_TOKEN_SYSTEM_PROPERTY = "spring.cloud.vault.token";

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext context) {

        Stream
                .of(context
                        .getEnvironment()
                        .getActiveProfiles())
                .filter(profile -> profile.equals(PROFILE))
                .findAny()
                .ifPresentOrElse(
                        profile -> {
                            System.out.printf("Setting system property %s for profile %s%n", VAULT_TOKEN_SYSTEM_PROPERTY, PROFILE);
                            System.setProperty(VAULT_TOKEN_SYSTEM_PROPERTY, getVaultToken(context));
                        },
                        () -> System.out.printf("Profile %s not active, skipping setting system property %s%n", PROFILE, VAULT_TOKEN_SYSTEM_PROPERTY));

    }

    private static String getVaultToken(ConfigurableApplicationContext context) {

        if (!"dev-fss".equals(System.getProperty(NAIS_CLUSTER_SYSTEM_PROPERTY))) {
            throw new VaultException("Attempting to get Vault token in cluster %s which is not onprem".formatted(System.getProperty(NAIS_CLUSTER_SYSTEM_PROPERTY)));
        }

        if (System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY) != null) {
            System.out.printf("Getting Vault token from system property %s%n", VAULT_TOKEN_ENVIRONMENT_PROPERTY);
            return System.getProperty(VAULT_TOKEN_SYSTEM_PROPERTY);
        }

        var environment = context.getEnvironment();
        if (!environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY, "").isEmpty()) {
            System.out.printf("Getting Vault token from environment property %s%n", VAULT_TOKEN_ENVIRONMENT_PROPERTY);
            return environment.getProperty(VAULT_TOKEN_ENVIRONMENT_PROPERTY);
        }

        var path = Paths.get("/var/run/secrets/nais.io/vault/vault_token");
        try {
            System.out.printf("Getting Vault token from file %s%n", path);
            return Files.readString(path).trim();
        } catch (IOException e) {
            throw new VaultException("Unable to read vault token from file %s".formatted(path.toString()), e);
        }

    }

}
