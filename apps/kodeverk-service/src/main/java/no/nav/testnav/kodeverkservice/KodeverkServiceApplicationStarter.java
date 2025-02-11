package no.nav.testnav.kodeverkservice;

import no.nav.testnav.libs.reactivecore.config.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class KodeverkServiceApplicationStarter {

    public static void main(String[] args) {
        new SpringApplicationBuilder(KodeverkServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}