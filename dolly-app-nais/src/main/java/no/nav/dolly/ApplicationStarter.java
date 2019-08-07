package no.nav.dolly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ApplicationStarter extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}