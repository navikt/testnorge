package no.nav.testnav.levendearbeidsforholdansettelse.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        InsecureJwtServerToServerConfiguration.class
})
@EnableAsync
public class ApplicationConfig {

}

