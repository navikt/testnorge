package no.nav.registre.testnorge.aaregsyntserviceproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@EnableZuulProxy
@SpringBootApplication
public class AaregSyntServiceProxyApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(AaregSyntServiceProxyApplicationStarter.class, args);
    }
}
