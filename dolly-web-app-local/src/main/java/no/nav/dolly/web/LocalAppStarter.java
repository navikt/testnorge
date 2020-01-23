package no.nav.dolly.web;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Profile;

@Profile("local")
public class LocalAppStarter {
    public static void main(String... arguments) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(arguments);
    }
}
