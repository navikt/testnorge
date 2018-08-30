package no.nav.identpool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApplicationStarter {
    public static void main(String[] arguments) {
        SpringApplication.run(ApplicationConfig.class, arguments);
    }
}
