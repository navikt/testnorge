package no.nav.testnav.libs.reactivefrontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.reactivefrontend.filter.IgnoreSetCookieFilter;
import no.nav.testnav.libs.reactivefrontend.filter.SessionTimeoutCookieFilter;

@Import({
        SessionTimeoutCookieFilter.class,
        IgnoreSetCookieFilter.class
})
@Configuration
public class FrontendConfig {
}