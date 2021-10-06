package no.nav.testnav.libs.reactivecore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivecore.filter.RequestLogger;
import no.nav.testnav.libs.reactivecore.router.InternalHandler;
import no.nav.testnav.libs.reactivecore.router.InternalRouter;

@Import({
        RequestLogger.class,
        InternalHandler.class,
        InternalRouter.class,
        ApplicationProperties.class
})
@Configuration
public class CoreConfig {
}
