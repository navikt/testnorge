package no.nav.registre.testnorge.identservice;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class IdentServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(IdentServiceApplicationStarter.class, args);
    }
}
