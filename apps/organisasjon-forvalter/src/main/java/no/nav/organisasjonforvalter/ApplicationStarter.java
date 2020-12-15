package no.nav.organisasjonforvalter;

import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Collections;
import java.util.Map;

public class ApplicationStarter {

    public static void main(String[] args) {

        Map<String, Object> properties = Collections.emptyMap();

        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            properties = PropertyReader.builder()
                    .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                    .readSecret("serviceuser.username", "/var/run/secrets/nais.io/orgforvalter/username")
                    .readSecret("serviceuser.password", "/var/run/secrets/nais.io/orgforvalter/password")
                    .build();
        }

        new SpringApplicationBuilder()
                .sources(AppConfig.class)
                .properties(properties)
                .run(args);
    }
}
