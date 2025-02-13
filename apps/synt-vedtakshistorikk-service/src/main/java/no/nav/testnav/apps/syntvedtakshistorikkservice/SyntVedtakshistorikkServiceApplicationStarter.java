package no.nav.testnav.apps.syntvedtakshistorikkservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class SyntVedtakshistorikkServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SyntVedtakshistorikkServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
