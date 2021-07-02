package no.nav.testnav.libs.frontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.frontend.filter.SessionTimeoutCookieFilter;
import no.nav.testnav.libs.frontend.router.InternalHandler;
import no.nav.testnav.libs.frontend.router.InternalRouter;

@Import({
        SessionTimeoutCookieFilter.class,
        InternalRouter.class,
        InternalHandler.class
})
@Configuration
public class FrontendConfig {
}