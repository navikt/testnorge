package no.nav.testnav.dollysearchservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.jwt.SecureOAuth2ServerToServerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerAutoConfiguration.class
})
public class ApplicationConfig {

}
