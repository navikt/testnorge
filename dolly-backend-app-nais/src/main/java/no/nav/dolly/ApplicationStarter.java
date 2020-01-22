package no.nav.dolly;

import java.util.Map;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import no.nav.fregpropertyreader.PropertyReader;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("DOLLY_DB_USERNAME", "/var/run/secrets/nais.io/db/username")
                .readSecret("DOLLY_DB_PASSWORD", "/var/run/secrets/nais.io/db/password")
                .readSecret("DOLLY_DB_URL", "/var/run/secrets/nais.io/dbPath/jdbc_url")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .properties(properties)
                .run(args);
    }
}