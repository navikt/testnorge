package no.nav.testnav.libs.database.config;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

import java.util.Optional;

@Slf4j
@Configuration
@ConditionalOnProperty("spring.cloud.vault.database.enabled")
public class FlywayConfiguration {

    @Bean
    public FlywayConfigurationCustomizer flywayConfig(
            VaultOperations vaultOperations,
            @Value("${spring.cloud.vault.database.role}") String role,
            @Value("${spring.cloud.vault.database.backend}") String backend,
            @Value("${spring.datasource.url}") String url
    ) {
        return configuration -> {
            var secretPath = format("%s/creds/%s", backend, role);
            var vaultResponse = Optional.ofNullable(vaultOperations.read(secretPath))
                    .map(VaultResponse::getData)
                    .orElseThrow(() -> new IllegalStateException(
                            format("Could not read credentials from Vault. Credential path: %s", secretPath)));

            var username = vaultResponse.get("username").toString();
            var password = vaultResponse.get("password").toString();

            configuration
                    .dataSource(url, username, password)
                    .initSql(format("SET ROLE \"%s\"", role));
            log.info("Flyway configured. With secret path {}", secretPath);
        };
    }
}
