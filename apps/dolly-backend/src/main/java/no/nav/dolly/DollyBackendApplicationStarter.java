package no.nav.dolly;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.HashMap;
import java.util.Map;

@EnableAutoConfiguration
@Slf4j
public class DollyBackendApplicationStarter {

    public static void main(String[] args) {

        Map<String, Object> properties = new HashMap<>();
        if (!"local".equals(System.getProperty("spring.profiles.active"))) {
            log.info("Profile er i if: {}", System.getProperty("spring.profiles.active"));
            properties = PropertyReader.builder()
                    .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                    .build();
        }
        log.info("Profile er: {}", System.getProperty("spring.profiles.active"));

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .properties(properties)
                .run(args);
    }
}