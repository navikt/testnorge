package no.nav.registre.testnorge.batchbestillingservice.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
})
public class ApplicationConfig {

}
