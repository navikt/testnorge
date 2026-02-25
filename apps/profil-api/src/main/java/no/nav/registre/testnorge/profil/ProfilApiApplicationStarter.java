package no.nav.registre.testnorge.profil;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.dolly.libs.security.config.ReactiveRequestContext;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        ReactiveRequestContext.class
})
public class ProfilApiApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProfilApiApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
