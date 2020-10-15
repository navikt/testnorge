package no.nav.dolly.local;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

import no.nav.dolly.ApplicationConfig;

@EnableAutoConfiguration
public class LocalApplicationStarter {
    public static void main(String... arguments) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(arguments);
    }
}