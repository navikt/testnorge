package no.nav.registre.testnorge.avhengighetsanalysefrontend.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.security.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.applikasjonsanalyse-service")
public class ApplikasjonsanalyseServiceProperties extends NaisServerProperties {
}