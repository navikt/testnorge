package no.nav.identpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Map;

@Slf4j
public class ApplicationStarter {

    public static void main(String[] arguments) {

        log.info("Starter opp...");

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                .readSecret("spring.datasource.username", "/var/run/secrets/nais.io/db/username")
                .readSecret("spring.datasource.password", "/var/run/secrets/nais.io/db/password")
                .readSecret("spring.datasource.url", "/var/run/secrets/nais.io/dbPath/jdbc_url")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class, ScheduleConfig.class)
                .properties(properties)
                .profiles("prod")
                .run(arguments);

        log.info("Startet.");
    }
}
