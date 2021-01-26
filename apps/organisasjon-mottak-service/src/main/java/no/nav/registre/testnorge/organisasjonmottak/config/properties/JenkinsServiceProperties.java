package no.nav.registre.testnorge.organisasjonmottak.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.jenkins")
public class JenkinsServiceProperties extends NaisServerProperties {
}
