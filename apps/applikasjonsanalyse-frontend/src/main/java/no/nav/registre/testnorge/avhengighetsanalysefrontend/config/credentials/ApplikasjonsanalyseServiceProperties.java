package no.nav.registre.testnorge.avhengighetsanalysefrontend.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.applikasjonsanalyse-service")
public class ApplikasjonsanalyseServiceProperties extends ServerProperties {
}