package no.nav.testnav.apps.syntsykemeldingapi;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class SyntSykemeldingApiApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SyntSykemeldingApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
