package no.nav.registre.testnorge.identservice;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;
import org.apache.catalina.authenticator.jaspic.AuthConfigFactoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import javax.security.auth.message.config.AuthConfigFactory;

import static java.util.Objects.isNull;

@Profile("prod")
@SpringBootApplication
@Import(value = {ApplicationCoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
public class IdentServiceApplicationStarter {
    public static void main(String[] args) {
        if (isNull(AuthConfigFactory.getFactory())) {
            AuthConfigFactory.setFactory(new AuthConfigFactoryImpl());
        }
        SpringApplication.run(IdentServiceApplicationStarter.class, args);
    }
}
