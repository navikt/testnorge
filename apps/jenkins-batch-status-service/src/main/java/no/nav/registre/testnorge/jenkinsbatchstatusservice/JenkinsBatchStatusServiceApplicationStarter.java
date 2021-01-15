package no.nav.registre.testnorge.jenkinsbatchstatusservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class JenkinsBatchStatusServiceApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(JenkinsBatchStatusServiceApplicationStarter.class, args);
    }
}