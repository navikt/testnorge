package no.nav.dolly;

import no.nav.testnav.libs.reactivecore.config.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = { ElasticsearchDataAutoConfiguration.class })
public class DollyBackendApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder(DollyBackendApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
