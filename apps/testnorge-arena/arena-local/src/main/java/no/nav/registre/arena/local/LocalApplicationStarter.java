package no.nav.registre.arena.local;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = "no.nav.registre.arena")
public class LocalApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .profiles("local")
                .run(args);
    }
}
