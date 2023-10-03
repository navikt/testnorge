package no.nav.testnav.proxies.arbeidsplassencvproxy.config;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumer.arbeidsplassencv")
public class ArbeidsplassenCVProperties extends ServerProperties {
}