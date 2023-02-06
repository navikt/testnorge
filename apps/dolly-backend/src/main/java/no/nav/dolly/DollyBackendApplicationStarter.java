package no.nav.dolly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@SpringBootApplication
// TODO: Clean up test code.
@EnableReactiveMethodSecurity
//@EnableWebFluxSecurity
//
public class DollyBackendApplicationStarter {

    public static void main(String[] args) {

        SpringApplication.run(DollyBackendApplicationStarter.class, args);
    }
}