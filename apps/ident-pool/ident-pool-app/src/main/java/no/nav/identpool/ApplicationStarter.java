package no.nav.identpool;

import java.util.Map;
import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.config.ScheduleConfig;

@Slf4j
public class ApplicationStarter {

    public static void main(String[] arguments) {

        log.info("Starter opp...");

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                .readSecret("oracle.datasource.username", "/var/run/secrets/nais.io/db/username")
                .readSecret("oracle.datasource.password", "/var/run/secrets/nais.io/db/password")
                .readSecret("oracle.datasource.url", "/var/run/secrets/nais.io/dbPath/jdbc_url")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class, ScheduleConfig.class)
                .properties(properties)
                .profiles("prod")
                .run(arguments);

        log.info("Startet.");
    }
}
