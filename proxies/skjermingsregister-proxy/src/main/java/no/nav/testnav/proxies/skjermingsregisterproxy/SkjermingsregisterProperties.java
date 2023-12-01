package no.nav.testnav.proxies.skjermingsregisterproxy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "app.skjermingsregister")
public class SkjermingsregisterProperties extends ServerProperties {
}
