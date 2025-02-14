package no.nav.testnav.apps.tpsmessagingservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TpsMessagingServiceApplicationStarter {
  
    public static void main(String[] args) {
        new SpringApplicationBuilder(TpsMessagingServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
