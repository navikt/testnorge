package no.nav.testnav.levendearbeidsforholdansettelse.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
@EnableAsync
public class ApplicationConfig {

}

