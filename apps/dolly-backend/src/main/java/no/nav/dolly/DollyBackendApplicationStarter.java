package no.nav.dolly;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import java.util.Map;

@SpringBootApplication
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class DollyBackendApplicationStarter extends SpringBootServletInitializer {

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