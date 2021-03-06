package no.nav.registre.skd;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LocalApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .profiles("local")
                .run(args);
    }
}
