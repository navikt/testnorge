package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class DisableAutomaticFlywayMigrationOnStartup {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        log.info("Disabling automatic Flyway migration on startup");
        return flyway -> {
            // Run flyway migration manually as needed.
        };
    }

}
