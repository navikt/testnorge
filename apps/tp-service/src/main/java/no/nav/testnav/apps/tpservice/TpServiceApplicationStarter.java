package no.nav.testnav.apps.tpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TpServiceApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TpServiceApplicationStarter.class, args);
    }
}
