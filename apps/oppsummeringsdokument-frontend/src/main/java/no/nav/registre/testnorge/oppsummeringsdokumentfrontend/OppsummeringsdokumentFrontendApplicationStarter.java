package no.nav.registre.testnorge.oppsummeringsdokumentfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class OppsummeringsdokumentFrontendApplicationStarter {


    public static void main(String[] args) {
        SpringApplication.run(OppsummeringsdokumentFrontendApplicationStarter.class, args);
    }
}
