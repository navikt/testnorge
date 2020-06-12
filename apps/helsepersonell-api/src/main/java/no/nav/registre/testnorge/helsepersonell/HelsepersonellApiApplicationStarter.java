package no.nav.registre.testnorge.helsepersonell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.nav.registre.testnorge.helsepersonell")
public class HelsepersonellApiApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(HelsepersonellApiApplicationStarter.class, args);
    }
}