package no.nav.pdl.forvalter.config.credentials;

import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "consumers.org-forvalter")
public class OrgForvalterServiceProperties extends ServerProperties {
}