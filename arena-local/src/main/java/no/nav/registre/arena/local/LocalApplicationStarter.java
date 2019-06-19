package no.nav.registre.arena.local;


import no.nav.registre.arena.core.config.AppConfig;
import org.springframework.boot.builder.SpringApplicationBuilder;


public class LocalApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(AppConfig.class)
                .profiles("local")
                .run(args);

    }
}
