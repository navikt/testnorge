package no.nav.registre.testnorge.libs.database.config;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.cloud.vault.config.databases.VaultDatabaseProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(value = {"spring.flyway.enabled", "spring.cloud.vault.database.enabled"})
public class FlywayConfiguration {

    @Bean
    public FlywayConfigurationCustomizer flywayConfig(
            VaultDatabaseProperties properties
    ) {
        return configuration -> {
            configuration.initSql(format("SET ROLE \"%s\"", properties.getRole()));
            log.info("Flyway configured.");
        };
    }
}
