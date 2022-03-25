package no.nav.testnav.libs.reactivefrontend.config;

import no.nav.testnav.libs.reactivefrontend.filter.SessionTimeoutCookieFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
        SessionTimeoutCookieFilter.class,
//        IgnoreSetCookieFilter.class
})
@Configuration
public class FrontendConfig {
}