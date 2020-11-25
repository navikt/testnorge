package no.nav.identpool;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import no.nav.identpool.config.ApplicationConfig;

@EnableAutoConfiguration
public class LocalApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(args);
    }
}
