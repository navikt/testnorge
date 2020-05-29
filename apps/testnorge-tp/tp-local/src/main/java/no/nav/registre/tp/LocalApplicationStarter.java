package no.nav.registre.tp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("local")
public class LocalApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .profiles("local")
                .run(args);
    }
}
