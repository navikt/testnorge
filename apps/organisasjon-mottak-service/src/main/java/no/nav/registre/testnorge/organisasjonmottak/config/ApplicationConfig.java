package no.nav.registre.testnorge.organisasjonmottak.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Import({CoreConfig.class})
@EnableScheduling
public class ApplicationConfig {
}