package no.nav.registre.testnav.dokarkivproxy;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class DokarkivProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(DokarkivProxyApplicationStarter.class, args);
    }
}
