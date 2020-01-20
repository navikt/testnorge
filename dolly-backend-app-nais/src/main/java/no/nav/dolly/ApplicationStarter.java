package no.nav.dolly;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .run(args);
    }
}