package no.nav.registre.testnorge.synt.sykemelding.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-arbeidsforhold-service")
public class ArbeidsforholdServiceProperties extends NaisServerProperties {
}