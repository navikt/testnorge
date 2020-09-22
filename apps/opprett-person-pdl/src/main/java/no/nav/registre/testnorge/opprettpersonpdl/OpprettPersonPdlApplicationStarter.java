package no.nav.registre.testnorge.opprettpersonpdl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class OpprettPersonPdlApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(OpprettPersonPdlApplicationStarter.class, args);
    }
}
