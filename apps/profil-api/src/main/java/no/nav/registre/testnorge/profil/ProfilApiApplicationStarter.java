package no.nav.registre.testnorge.profil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@SpringBootApplication
@Import({ApplicationCoreConfig.class, SecureOAuth2ServerToServerConfiguration.class})
public class ProfilApiApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ProfilApiApplicationStarter.class, args);
    }
}
