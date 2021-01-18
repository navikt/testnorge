package no.nav.registre.testnorge.batchadeoproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class BatchAdeoProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(BatchAdeoProxyApplicationStarter.class, args);
    }
}
