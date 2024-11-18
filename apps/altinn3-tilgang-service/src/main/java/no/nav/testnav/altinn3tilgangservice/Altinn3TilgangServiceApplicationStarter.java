package no.nav.testnav.altinn3tilgangservice;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableWebFlux
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@RequiredArgsConstructor
@SpringBootApplication
public class Altinn3TilgangServiceApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(Altinn3TilgangServiceApplicationStarter.class, args);
    }
}
