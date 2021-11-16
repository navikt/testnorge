package no.nav.dolly.web.config;

import no.nav.testnav.libs.reactivesessionsecurity.config.OidcSessionRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdRedisSessionConfiguration;

@Configuration
@Profile("prod")
@Import({
        OicdRedisSessionConfiguration.class,
        OidcSessionRepositoryConfiguration.class
})
public class ProdConfig {
}
