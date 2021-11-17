package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;

@Configuration
@Profile({"dev", "test"})
@Import({
        OicdInMemorySessionConfiguration.class
})
public class DevConfig {
}
