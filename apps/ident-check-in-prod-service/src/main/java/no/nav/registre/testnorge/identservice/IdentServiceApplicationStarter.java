package no.nav.registre.testnorge.identservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Profile("prod")
@SpringBootApplication
public class IdentServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(IdentServiceApplicationStarter.class, args);
    }
}
