package no.nav.testnav.proxies.aareg;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(CoreConfig.class)
@SpringBootApplication
@EnableConfigurationProperties
public class AaregProxyApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AaregProxyApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
