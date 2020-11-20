package no.nav.registre.testnorge.organisasjonmottakservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class OrganisasjonMottakServiceApplicationStarter {
    public static void main(String[] args) {
        log.info("{}", String.join(",", System.getenv().keySet()));
        SpringApplication.run(OrganisasjonMottakServiceApplicationStarter.class, args);
    }
}
