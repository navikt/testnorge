package no.nav.registre.arena.cloud;


import no.nav.registre.arena.core.config.AppConfig;
import org.springframework.boot.SpringApplication;

public class ApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppConfig.class, args);
    }

}
