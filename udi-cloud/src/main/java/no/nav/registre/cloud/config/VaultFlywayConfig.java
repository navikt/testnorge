package no.nav.registre.cloud.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.VaultOperations;

import static java.lang.String.format;

@Slf4j
@Configuration
@ConditionalOnClass(VaultOperations.class)
@EnableConfigurationProperties(VaultFlywayProperties.class)
public class VaultFlywayConfig {

    @Bean
    FlywayConfigurationCustomizer flywayVaultConfiguration(VaultOperations vaultOperations, VaultFlywayProperties flywayProperties, @Value("${spring.datasource.url}") String url) {
        return configuration -> {
            val secretPath = format("%s/%s", flywayProperties.getBackend(), flywayProperties.getRole());

            val vaultResponse = vaultOperations.read(secretPath);
            val username = vaultResponse.getData().get("username").toString();
            val password = vaultResponse.getData().get("password").toString();

            log.info("Vault: Flyway configured with credentials from Vault. Credential path: {}", secretPath);

            configuration
                    .dataSource(url, username, password)
                    .initSql(format("SET ROLE \"%s\"", flywayProperties.getRole()));
        };
    }
}
