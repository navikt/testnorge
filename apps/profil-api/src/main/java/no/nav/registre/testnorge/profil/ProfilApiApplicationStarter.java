package no.nav.registre.testnorge.profil;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
@Import({ ApplicationCoreConfig.class })
public class ProfilApiApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProfilApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
