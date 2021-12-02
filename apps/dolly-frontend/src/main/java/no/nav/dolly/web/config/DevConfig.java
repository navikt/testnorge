package no.nav.dolly.web.config;

import no.nav.dolly.web.config.filter.WebSessionFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;

@Configuration
@Profile({"dev", "test"})
@Import({
        OicdInMemorySessionConfiguration.class,
        WebSessionConfig.class,
        WebSessionFilter.class
})
public class DevConfig {
}
