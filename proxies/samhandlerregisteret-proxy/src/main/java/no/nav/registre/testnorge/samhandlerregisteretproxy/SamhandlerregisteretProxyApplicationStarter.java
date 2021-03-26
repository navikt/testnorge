package no.nav.registre.testnorge.samhandlerregisteretproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class SamhandlerregisteretProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(SamhandlerregisteretProxyApplicationStarter.class, args);
    }
}