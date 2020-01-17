package no.nav.dolly;

import java.util.Map;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import no.nav.fregpropertyreader.PropertyReader;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        Map<String, Object> properties = PropertyReader.builder()
                .readSecret("spring.datasource.username", "/var/run/secrets/nais.io/db/username")
                .readSecret("spring.datasource.password", "/var/run/secrets/nais.io/db/password")
                .readSecret("spring.datasource.url", "/var/run/secrets/nais.io/dbPath/jdbc_url")
                .readSecret("credentials.preprod.username", "/var/run/secrets/nais.io/preprod/username")
                .readSecret("credentials.preprod.password", "/var/run/secrets/nais.io/preprod/password")
                .readSecret("credentials.test.username", "/var/run/secrets/nais.io/test/username")
                .readSecret("credentials.test.password", "/var/run/secrets/nais.io/test/password")
                .readSecret("jira.username","/var/run/secrets/nais.io/jira/username")
                .readSecret("jira.password", "/var/run/secrets/nais.io/jira/password")
                .build();

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .properties(properties)
                .run(args);
    }
}