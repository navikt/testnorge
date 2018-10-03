package no.nav.registre.orkestratoren;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class NaisApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(NaisApplicationStarter.class)
                .profiles("local")
                .run(args);
    }
}