package no.nav.testnav.levendearbeidsforholdscheduler.config;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ApplicationCoreConfig.class})
public class ApplicationConfig {

}
