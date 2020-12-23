package no.nav.registre.testnorge.arbeidsforholdfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class ArbeidsforholdFrontendApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(ArbeidsforholdFrontendApplicationStarter.class, args);
    }
}
