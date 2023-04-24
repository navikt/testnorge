package no.nav.testnav.apps.oppsummeringsdokumentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

@SpringBootApplication(exclude = { ElasticsearchRestClientAutoConfiguration.class })
public class OppsummeringsdokumentServiceApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(OppsummeringsdokumentServiceApplicationStarter.class, args);
    }
}
