package no.nav.registre.testnorge.tilbakemeldingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class TilbakemeldingApiApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TilbakemeldingApiApplicationStarter.class, args);
    }

}
