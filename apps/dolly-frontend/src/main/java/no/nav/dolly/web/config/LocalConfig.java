package no.nav.dolly.web.config;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdInMemorySessionConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "test"})
@Import({
        OicdInMemorySessionConfiguration.class
})
public class LocalConfig {
}
