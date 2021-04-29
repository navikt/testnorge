package no.nav.registre.testnorge.syntrestproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class SyntrestProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(SyntrestProxyApplicationStarter.class, args);
    }
}
