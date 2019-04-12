package no.nav.registre.aaregstub;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class LocalApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
                .properties("spring.cloud.vault.token=123456")
                .profiles("local")
                .run(args);
    }
}
