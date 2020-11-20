package no.nav.registre.testnorge.organisasjonmottakservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.stream.Collectors;

@SpringBootApplication
public class OrganisasjonMottakServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(OrganisasjonMottakServiceApplicationStarter.class, args);
    }
}
