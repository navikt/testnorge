package no.nav.identpool;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(args);
    }
}
