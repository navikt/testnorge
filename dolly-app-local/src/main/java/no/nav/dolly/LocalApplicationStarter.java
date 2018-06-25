package no.nav.dolly;

import no.nav.ApplicationConfig;

import org.springframework.boot.SpringApplication;

public class LocalApplicationStarter {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApplicationConfig.class);
        application.setAdditionalProfiles("h2");
        application.run(args);
    }
}
