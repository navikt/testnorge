package no.nav.registre.testnorge.rapportering.config;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.core.VaultOperations;

@Slf4j
@Profile("prod")
@Configuration
@ConditionalOnClass(VaultOperations.class)
@EnableConfigurationProperties(VaultFlywayProperties.class)
public class VaultFlywayConfig {

    @Bean
    FlywayConfigurationCustomizer flywayVaultConfiguration(VaultOperations vaultOperations, VaultFlywayProperties flywayProperties, @Value("${spring.datasource.url}") String url) {
        return configuration -> {
            var secretPath = format("%s/%s", flywayProperties.getBackend(), flywayProperties.getRole());

            var vaultResponse = vaultOperations.read(secretPath);
            var username = vaultResponse.getData().get("username").toString();
            var password = vaultResponse.getData().get("password").toString();

            log.info("Vault: Flyway configured with credentials from Vault. Credential path: {}", secretPath);
            configuration

                    .dataSource(url, username, password)
                    .initSql(format("SET ROLE \"%s\"", flywayProperties.getRole()));
        };
    }
}
