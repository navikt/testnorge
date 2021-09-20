package no.nav.testnav.libs.reactivesecurity.properties;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.idporten")
@ConditionalOnProperty("spring.security.oauth2.resourceserver.idporten.issuer-uri")
public class IdportenResourceServerProperties extends ResourceServerProperties {

}