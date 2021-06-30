package no.nav.registre.testnorge.endringsmeldingfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class EndringsmeldingFrontendApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(EndringsmeldingFrontendApplicationStarter.class, args);
    }
}
