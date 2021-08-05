package no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.aareg-synt-services")
public class AaregSyntServiceProperties extends NaisServerProperties {
}