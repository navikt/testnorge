package no.nav.testnav.levendearbeidsforholdscheduler.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class,
        SecureOAuth2ServerToServerConfiguration.class
})
public class ApplicationConfig {

}
