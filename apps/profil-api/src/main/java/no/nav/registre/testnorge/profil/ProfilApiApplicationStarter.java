package no.nav.registre.testnorge.profil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;

@SpringBootApplication
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ProfilApiApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ProfilApiApplicationStarter.class, args);
    }
}
