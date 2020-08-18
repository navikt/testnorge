package no.nav.registre.testnorge.hendelse.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.vault.config.LeasingSecretBackendMetadata;
import org.springframework.cloud.vault.config.SecretBackendConfigurer;
import org.springframework.cloud.vault.config.VaultConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.util.PropertyTransformer;

import java.util.Map;

import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.RENEW;
import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.ROTATE;
import static org.springframework.vault.core.lease.domain.RequestedSecret.renewable;
import static org.springframework.vault.core.util.PropertyTransformers.propertyNamePrefix;

/**
 * <p>This class assumes that a handful of Vault secret paths are available in the form of application properties
 * and configures a more specialized set of property sources to fetch than the default Spring Cloud Vault configuration.
 * Once the properties have been retrieved, it does some extra mapping to make them fit in with the Spring
 * environment.</p>
 *
 * <ol>
 *     <li>{@code VAULT_SERVICEUSER_PATH}<br/>Will be used to get the serviceuser credentials. The resulting
 *     properties are stored as follows:
 *     <ul>
 *         <li>{@code app.serviceuser.username}</li>
 *         <li>{@code app.serviceuser.password}</li>
 *     </ul>
 *     </li>
 *     <li>
 *         {@code VAULT_APP_PATH}<br/> will be used to fetch namespace-specific secrets from the versioned
 *         backends. The properties will be presented to the Spring context as-is, with no further mapping applied to
 *         them.
 *     </li>
 *     <li>
 *         {@code VAULT_DATABASE_ADMIN_PATH}<br/>Will be used to get admin credentials in order to run Flyway
 *         migration scripts, which is not allowed with the normal database user. Additionally, it makes sure Flyway
 *         sets the role correctly before the migration scripts are run, so that whatever is created is not only owned
 *         by the temporary user that was provisioned by Vault. The resulting properties are stored as follows:
 *         <ul>
 *             <li>{@code spring.flyway.user}</li>
 *             <li>{@code spring.flyway.password}</li>
 *             <li>{@code spring.flyway.init-sqls}</li>
 *         </ul>
 *     </li>
 *     <li>
 *         {@code VAULT_DATABASE_USER_PATH}<br/>Will be used to provision a set of normal user credentials that have
 *         can perform CRUD operations, but which doesn't have privileges to manipulate schema. The resulting properties
 *         are stored as follows:
 *         <ul>
 *             <li>{@code spring.datasource.username}</li>
 *             <li>{@code spring.datasource.password}</li>
 *         </ul>
 *     </li>
 * </ol>
 */
@RequiredArgsConstructor
public class NavVaultConfigurer {

    @Bean
    @Autowired
    VaultConfigurer vaultConfigurer(
            @Value("${VAULT_SERVICEUSER_PATH:#{null}}") String serviceuserPath,
            @Value("${VAULT_APP_PATH:#{null}}") String appSecretsPath,
            @Value("${VAULT_DATABASE_ADMIN_PATH:#{null}}") String databaseAdminPath,
            @Value("${VAULT_DATABASE_USER_PATH:#{null}}") String databaseUserPath) {

        return new VaultConfigurer() {
            @Override
            public void addSecretBackends(SecretBackendConfigurer configurer) {
                addServiceuserSecrets(configurer);
                addAppSecrets(configurer);
                addDatabaseSecrets(configurer);
            }

            private void addServiceuserSecrets(SecretBackendConfigurer configurer) {
                configurer.add(renewable(serviceuserPath), propertyNamePrefix("app.serviceuser."));
            }

            private void addAppSecrets(SecretBackendConfigurer configurer) {
                configurer.add(renewable(appSecretsPath));
            }

            private void addDatabaseSecrets(SecretBackendConfigurer configurer) {
                configurer.add(new PostgreSqlBackendMetadata(databaseAdminPath));
                configurer.add(new PostgreSqlBackendMetadata(databaseUserPath));
            }
        };
    }

    @Slf4j(topic = "secureLogger")
    @RequiredArgsConstructor
    private static class PostgreSqlBackendMetadata implements LeasingSecretBackendMetadata {

        private final String secretPath;

        @Override
        public String getName() {
            return secretPath;
        }

        @Override
        public String getPath() {
            return secretPath;
        }

        @Override
        public PropertyTransformer getPropertyTransformer() {
            if (secretPath.endsWith("-admin")) {
                return input -> {
                    Map<String, Object> propertyMap = (Map) input;
                    propertyMap.put("spring.flyway.user", propertyMap.remove("username"));
                    propertyMap.put("spring.flyway.password", propertyMap.remove("password"));
                    propertyMap.put("spring.flyway.init-sqls", String.format("SET ROLE '%s'", resolveRole()));
                    return propertyMap;
                };
            } else {
                return propertyNamePrefix("spring.datasource.");
            }
        }

        @Override
        public Map<String, String> getVariables() {
            String backend = secretPath.substring(0, secretPath.indexOf("/creds/"));
            String key = secretPath.substring(backend.length() + 1);
            return Map.of("backend", backend, "key", key);
        }

        @Override
        public RequestedSecret.Mode getLeaseMode() {
            if (secretPath.endsWith("-admin")) {
                return RENEW;
            } else {
                return ROTATE;
            }
        }

        private String resolveRole() {
            return secretPath.substring(secretPath.lastIndexOf('/') + 1);
        }

        @Override
        public void beforeRegistration(RequestedSecret secret, SecretLeaseContainer container) {
            log.info("Adding a lease listener to {}", secret);
            container.addLeaseListener(leaseEvent -> {
                log.info("{}", leaseEvent);
            });
        }
    }
}