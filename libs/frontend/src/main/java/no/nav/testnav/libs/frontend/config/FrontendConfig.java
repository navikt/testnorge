package no.nav.testnav.libs.frontend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.frontend.filter.SessionTimeoutCookieFilter;

@Import({
        SessionTimeoutCookieFilter.class
})
@Configuration
public class FrontendConfig {
}