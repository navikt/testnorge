package no.nav.registre.arena.local;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.nav.registre.arena")
public class LocalApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(LocalApplicationStarter.class);
    }
}
