package no.nav.registre.testnorge.organisasjonmottakservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class OrganisasjonMottakServiceApplicationStarter {
    public static void main(String[] args) {
        log.info("############################################");
        log.info("{}", String.join(",", System.getProperties().keySet().stream().map(Object::toString).collect(Collectors.toSet())));
        log.info("############################################");
        SpringApplication.run(OrganisasjonMottakServiceApplicationStarter.class, args);
    }
}
