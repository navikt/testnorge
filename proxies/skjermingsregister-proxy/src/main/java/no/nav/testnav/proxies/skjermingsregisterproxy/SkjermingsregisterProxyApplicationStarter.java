package no.nav.testnav.proxies.skjermingsregisterproxy;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(CoreConfig.class)
public class SkjermingsregisterProxyApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(SkjermingsregisterProxyApplicationStarter.class, args);
    }

}