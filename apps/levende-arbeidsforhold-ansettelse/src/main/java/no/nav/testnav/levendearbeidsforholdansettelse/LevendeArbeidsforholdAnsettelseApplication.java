package no.nav.testnav.levendearbeidsforholdansettelse;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableAsync
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@EnableWebFlux
@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class LevendeArbeidsforholdAnsettelseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LevendeArbeidsforholdAnsettelseApplication.class, args);
    }

}