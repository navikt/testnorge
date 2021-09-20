package no.nav.testnav.libs.reactivesecurity.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.aad")
public class AzureAdResourceServerProperties extends ResourceServerProperties {

}