package no.nav.testnav.apps.syntvedtakshistorikkservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@Import({CoreConfig.class})
@SpringBootApplication
public class SyntVedtakshistorikkServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SyntVedtakshistorikkServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
