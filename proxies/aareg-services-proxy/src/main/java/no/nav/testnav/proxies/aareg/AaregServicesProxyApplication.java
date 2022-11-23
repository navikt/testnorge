package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(CoreConfig.class)
@SpringBootApplication
public class AaregServicesProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AaregServicesProxyApplication.class, args);
    }

}