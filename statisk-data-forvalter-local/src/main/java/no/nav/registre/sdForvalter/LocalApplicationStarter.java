package no.nav.registre.sdForvalter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Slf4j
@SpringBootApplication
@Profile("local")
public class LocalApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(LocalApplicationStarter.class, args);
    }
}

