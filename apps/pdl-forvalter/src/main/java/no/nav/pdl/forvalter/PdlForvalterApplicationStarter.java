package no.nav.pdl.forvalter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = "no.nav.pdl.forvalter.database.repository")
@SpringBootApplication
public class PdlForvalterApplicationStarter {

    public static void main(String[] args) {

        SpringApplication.run(PdlForvalterApplicationStarter.class, args);
    }
}