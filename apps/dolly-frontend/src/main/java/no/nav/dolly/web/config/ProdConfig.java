package no.nav.dolly.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdRedisSessionConfiguration;

@Configuration
@Profile("prod")
@Import({
        OicdRedisSessionConfiguration.class
})
public class ProdConfig {
}
