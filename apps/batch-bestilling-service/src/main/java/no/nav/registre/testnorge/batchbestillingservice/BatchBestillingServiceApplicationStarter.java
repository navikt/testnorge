package no.nav.registre.testnorge.batchbestillingservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BatchBestillingServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(BatchBestillingServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
