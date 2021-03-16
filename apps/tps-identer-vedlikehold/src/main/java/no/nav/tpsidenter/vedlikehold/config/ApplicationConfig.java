package no.nav.tpsidenter.vedlikehold.config;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({ApplicationCoreConfig.class,})
@EnableScheduling
public class ApplicationConfig {

}