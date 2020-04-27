package no.nav.brregstub.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponse;

import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@Configuration
@ConditionalOnProperty("spring.flyway.enabled")
@EnableConfigurationProperties(VaultFlywayProperties.class)
public class VaultFlywayConfig {

    @Bean
    FlywayConfigurationCustomizer flywayVaultConfiguration(VaultOperations vaultOperations,
                                                           VaultFlywayProperties flywayProperties,
                                                           @Value("${spring.datasource.url}") String url) {
        return configuration -> {
            var secretPath = format("%s/%s", flywayProperties.getBackend(), flywayProperties.getRole());

            var vaultResponse = Optional.ofNullable(vaultOperations.read(secretPath))
                    .map(VaultResponse::getData)
                    .orElseThrow(() -> new IllegalStateException(
                            format("Could not read credentials from Vault. Credential path: %s", secretPath)));

            var username = vaultResponse.get("username").toString();
            var password = vaultResponse.get("password").toString();

            configuration
                    .dataSource(url, username, password)
                    .initSql(format("SET ROLE \"%s\"", flywayProperties.getRole()));

            log.info("Vault: Flyway configured with credentials from Vault. Credential path: {}", secretPath);
        };
    }
}
