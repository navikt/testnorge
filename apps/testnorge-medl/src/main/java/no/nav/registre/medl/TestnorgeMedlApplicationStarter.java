package no.nav.registre.medl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TestnorgeMedlApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TestnorgeMedlApplicationStarter.class, args);
    }
}