package no.nav.organisasjonforvalter;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class ApplicationStarter {

    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(AppConfig.class)
                .run(args);
    }
}
