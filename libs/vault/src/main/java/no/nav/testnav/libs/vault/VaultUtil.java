package no.nav.testnav.libs.vault;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
@Slf4j
public final class VaultUtil {

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

    public static void initCloudVaultToken() {
        System.setProperty(VAULT_TOKEN_SYSTEM_PROPERTY, getVaultToken());
    }

}
