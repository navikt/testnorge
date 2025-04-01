package no.nav.testnav.libs.reactivecore.config;

import no.nav.testnav.libs.reactivecore.filter.RequestLogger;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        RequestLogger.class,
        WebClientLogger.class,
        ApplicationProperties.class
})
@Configuration
public class CoreConfig {
}
