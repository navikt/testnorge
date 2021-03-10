package no.nav.registre.testnorge.synt.sykemelding.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-arbeidsforhold-api")
public class ArbeidsforholdApiServiceProperties extends NaisServerProperties {
}