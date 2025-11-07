package no.nav.testnav.pdldatalagreservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PdlDataLagreServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PdlDataLagreServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}