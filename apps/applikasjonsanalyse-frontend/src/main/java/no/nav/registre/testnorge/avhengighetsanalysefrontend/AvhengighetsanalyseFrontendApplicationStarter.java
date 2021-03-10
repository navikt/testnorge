package no.nav.registre.testnorge.avhengighetsanalysefrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class AvhengighetsanalyseFrontendApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(AvhengighetsanalyseFrontendApplicationStarter.class, args);
    }
}
