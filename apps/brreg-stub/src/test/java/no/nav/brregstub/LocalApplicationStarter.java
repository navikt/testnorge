package no.nav.brregstub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LocalApplicationStarter {
    public static void main(String... args) {
        new SpringApplicationBuilder()
                .profiles("local")
                .sources(ApplicationConfig.class)
                .run(args);
    }
}
