package no.nav.registre.tp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TestnorgeTpApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TestnorgeTpApplicationStarter.class, args);
    }
}
