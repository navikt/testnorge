package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(CoreConfig.class)
@SpringBootApplication
@EnableConfigurationProperties
public class AaregProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AaregProxyApplication.class, args);
    }
}