package no.nav.testnav.proxies.sykemeldingapiproxy.config;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.sykemelding-api")
public class SykemeldingApiProperties extends ServerProperties {
}