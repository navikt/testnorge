package no.nav.registre.skd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestnorgeSkdApplicationStarter {

    @Autowired
    HealthContributorRegistry healthContributorRegistry;

    public static void main(String[] args) {
        SpringApplication.run(TestnorgeSkdApplicationStarter.class, args);
    }
}
