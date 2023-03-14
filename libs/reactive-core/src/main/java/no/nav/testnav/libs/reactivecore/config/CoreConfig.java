package no.nav.testnav.libs.reactivecore.config;

import no.nav.testnav.libs.reactivecore.filter.RequestLogger;
import no.nav.testnav.libs.reactivecore.router.InternalHandler;
import no.nav.testnav.libs.reactivecore.router.InternalRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        RequestLogger.class,
        InternalHandler.class,
        InternalRouter.class,
        ApplicationProperties.class,
        WebClientConfig.class
})
@Configuration
public class CoreConfig {
}
