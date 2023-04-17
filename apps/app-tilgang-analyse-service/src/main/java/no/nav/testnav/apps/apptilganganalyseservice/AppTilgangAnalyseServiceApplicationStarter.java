package no.nav.testnav.apps.apptilganganalyseservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableWebFlux
@SpringBootApplication
public class AppTilgangAnalyseServiceApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppTilgangAnalyseServiceApplicationStarter.class, args);
    }
}
