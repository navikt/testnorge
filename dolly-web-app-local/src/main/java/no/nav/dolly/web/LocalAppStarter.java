package no.nav.dolly.web;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalAppStarter {
    public static void main(String... arguments) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(arguments);
    }
}
