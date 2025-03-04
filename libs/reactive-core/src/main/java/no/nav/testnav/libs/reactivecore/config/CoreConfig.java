package no.nav.testnav.libs.reactivecore.config;

import no.nav.testnav.libs.reactivecore.filter.RequestLogger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        RequestLogger.class,
        ApplicationProperties.class,
        WebClientConfig.class
})
@Configuration
public class CoreConfig {
}
