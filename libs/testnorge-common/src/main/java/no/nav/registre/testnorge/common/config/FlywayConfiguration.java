package no.nav.registre.testnorge.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("spring.flyway.enabled")
public class FlywayConfiguration {

    @Bean
    public FlywayConfigurationCustomizer flywayConfig(@Value("${spring.cloud.vault.database.role}") String role) {
        return c -> c.initSql(String.format("SET ROLE \"%s\"", role));
    }
}
