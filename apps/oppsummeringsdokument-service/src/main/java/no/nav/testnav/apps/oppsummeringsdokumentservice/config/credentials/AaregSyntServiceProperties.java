package no.nav.testnav.apps.oppsummeringsdokumentservice.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.aareg-synt-services")
public class AaregSyntServiceProperties extends ValidatingServerProperties {
}