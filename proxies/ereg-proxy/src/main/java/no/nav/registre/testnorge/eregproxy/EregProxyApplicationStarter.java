package no.nav.registre.testnorge.eregproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class EregProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(EregProxyApplicationStarter.class, args);
    }
}