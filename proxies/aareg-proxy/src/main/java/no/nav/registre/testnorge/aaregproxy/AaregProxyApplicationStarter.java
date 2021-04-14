package no.nav.registre.testnorge.aaregproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class AaregProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(AaregProxyApplicationStarter.class, args);
    }
}