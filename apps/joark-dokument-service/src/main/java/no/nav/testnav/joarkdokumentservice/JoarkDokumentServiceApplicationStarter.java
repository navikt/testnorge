package no.nav.testnav.joarkdokumentservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@SpringBootApplication
public class JoarkDokumentServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(JoarkDokumentServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

}