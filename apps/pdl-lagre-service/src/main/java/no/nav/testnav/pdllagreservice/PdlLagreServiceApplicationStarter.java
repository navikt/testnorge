package no.nav.testnav.pdllagreservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = ElasticsearchDataAutoConfiguration.class)
public class PdlLagreServiceApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder(PdlLagreServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
