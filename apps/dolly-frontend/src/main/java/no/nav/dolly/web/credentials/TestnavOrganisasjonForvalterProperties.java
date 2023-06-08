package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-forvalter")
public class TestnavOrganisasjonForvalterProperties extends ValidatingServerProperties {
}