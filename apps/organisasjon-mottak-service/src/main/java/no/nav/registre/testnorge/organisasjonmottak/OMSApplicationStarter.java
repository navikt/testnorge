package no.nav.registre.testnorge.organisasjonmottak;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OMSApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(OMSApplicationStarter.class, args);

    }
}
