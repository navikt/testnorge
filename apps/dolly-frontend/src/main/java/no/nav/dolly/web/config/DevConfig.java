package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
import no.nav.testnav.libs.reactivesessionsecurity.config.OicdRedisSessionConfiguration;

@Configuration
@Profile("dev")
@Import({
        OicdInMemorySessionConfiguration.class
})
public class DevConfig {
}
