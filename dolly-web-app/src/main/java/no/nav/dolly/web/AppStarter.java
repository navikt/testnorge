package no.nav.dolly.web;

import org.springframework.boot.SpringApplication;

public class AppStarter {
    public static void main(String... args) {
        SpringApplication application = new SpringApplication(ApplicationConfig.class);
        application.setAdditionalProfiles("nais");
        application.run(args);
    }
}
