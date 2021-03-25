package no.nav.registre.testnorge.pdlproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class PdlProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(PdlProxyApplicationStarter.class, args);
    }
}
