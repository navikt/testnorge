package no.nav.registre.testnorge.profil;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ApplicationCoreConfig.class})
public class ProfilApiApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ProfilApiApplicationStarter.class, args);
    }
}
