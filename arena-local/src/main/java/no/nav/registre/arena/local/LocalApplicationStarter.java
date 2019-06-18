package no.nav.registre.arena.local;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .profiles("local")
                .run(args);
    }

}
