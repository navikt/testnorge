package no.nav.registre.testnorge.endringsmeldingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.AnalysisFSSAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import({ApplicationCoreConfig.class, AnalysisFSSAutoConfiguration.class})
public class ApplicationConfig {

}
