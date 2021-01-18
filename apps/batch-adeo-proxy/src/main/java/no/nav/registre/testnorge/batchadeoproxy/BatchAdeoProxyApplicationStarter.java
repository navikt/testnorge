package no.nav.registre.testnorge.batchadeoproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import no.nav.registre.testnorge.libs.core.util.VaultUtil;

@EnableZuulProxy
@SpringBootApplication
public class BatchAdeoProxyApplicationStarter {
    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(BatchAdeoProxyApplicationStarter.class, args);
    }
}
