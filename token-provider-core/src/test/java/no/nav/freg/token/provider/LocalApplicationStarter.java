package no.nav.freg.token.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocalApplicationStarter {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(LocalApplicationStarter.class, args);
    }
}
