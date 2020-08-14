package no.nav.registre.udistub.local;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import no.nav.registre.udistub.ApplicationConfig;

@SpringBootApplication
public class LocalApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .profiles("local")
                .sources(ApplicationConfig.class)
                .run(args);
    }
}
