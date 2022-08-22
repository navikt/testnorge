package no.nav.registre.testnorge.organisasjonmottak.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.jenkins")
public class JenkinsServiceProperties extends ServerProperties {
}
