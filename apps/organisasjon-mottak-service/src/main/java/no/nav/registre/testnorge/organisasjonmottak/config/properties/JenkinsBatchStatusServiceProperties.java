package no.nav.registre.testnorge.organisasjonmottak.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.jenkins-batch-status-service")
public class JenkinsBatchStatusServiceProperties extends NaisServerProperties {
}
