package no.nav.registre.testnorge.jenkinsbatchstatusservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.RemoteServiceProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.jenkins")
public class JenkinsServiceProperties extends RemoteServiceProperties {
}
