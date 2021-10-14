package no.nav.pdl.forvalter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
@EnableR2dbcRepositories
public class PdlForvalterApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(PdlForvalterApplicationStarter.class, args);
    }
}