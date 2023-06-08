package no.nav.pdl.forvalter.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ValidatingServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.org-forvalter")
public class OrgForvalterServiceProperties extends ValidatingServerProperties {
}