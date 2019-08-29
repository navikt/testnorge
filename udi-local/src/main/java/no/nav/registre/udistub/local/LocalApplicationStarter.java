package no.nav.registre.udistub.local;

import no.nav.registre.udistub.ApplicationConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .profiles("local")
                .sources(ApplicationConfig.class)
                .run(args);
    }
}
