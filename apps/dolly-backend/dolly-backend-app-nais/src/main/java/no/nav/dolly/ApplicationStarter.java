package no.nav.dolly;

import java.util.Map;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("spring.cloud.vault.token", "/var/run/secrets/nais.io/vault/vault_token")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .properties(properties)
                .run(args);
    }
}