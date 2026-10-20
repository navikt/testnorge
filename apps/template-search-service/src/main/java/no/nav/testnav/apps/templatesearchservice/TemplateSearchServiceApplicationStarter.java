package no.nav.testnav.apps.templatesearchservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.nais.NaisPkcs8ConversionInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableWebFlux
@SpringBootApplication
public class TemplateSearchServiceApplicationStarter {

    static void main(String[] args) {
        new SpringApplicationBuilder(TemplateSearchServiceApplicationStarter.class)
                .initializers(
                        new NaisEnvironmentApplicationContextInitializer(),
                        new NaisPkcs8ConversionInitializer())
                .run(args);
    }
}
