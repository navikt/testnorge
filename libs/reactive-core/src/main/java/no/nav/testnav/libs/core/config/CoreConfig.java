package no.nav.testnav.libs.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.core.filter.RequestLogger;
import no.nav.testnav.libs.core.router.InternalHandler;
import no.nav.testnav.libs.core.router.InternalRouter;

@Import({
        RequestLogger.class,
        InternalHandler.class,
        InternalRouter.class,
        ApplicationProperties.class
})
@Configuration
public class CoreConfig {
}
