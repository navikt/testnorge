package no.nav.registre.testnorge.fastedatafrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class FasteDataFrontendApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(FasteDataFrontendApplicationStarter.class, args);
    }
}
