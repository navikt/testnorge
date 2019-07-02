package no.nav.registre.local;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(scanBasePackages = {"no.nav.registre.core"})
public class LocalApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .profiles("local")
                .sources(LocalApplicationStarter.class)
                .run(args);
    }
}
