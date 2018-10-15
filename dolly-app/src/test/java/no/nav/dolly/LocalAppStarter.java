package no.nav.dolly;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("no.nav.dolly")
public class LocalAppStarter {

    public static void main(String[] args) {

        System.setProperty("spring.profiles.active", "u");

        ApplicationStarter.main(args);

    }
}

