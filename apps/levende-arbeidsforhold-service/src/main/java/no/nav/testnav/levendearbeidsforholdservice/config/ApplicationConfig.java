package no.nav.testnav.levendearbeidsforholdservice.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CoreConfig.class})
public class ApplicationConfig {

}
