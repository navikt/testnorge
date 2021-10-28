package no.nav.dolly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class DollyBackendApplicationStarter {
    public static void main(String[] args) {

        SpringApplication.run(ApplicationConfig.class, args);
    }
}