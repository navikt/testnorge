package no.nav.testnav.apps.syntsykemeldingapi.consumer.credential;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.synt-sykemelding")
public class SyntSykemeldingProperties extends ServerProperties {
}
