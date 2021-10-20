package no.nav.testnav.libs.servletsecurity.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.domain.ResourceServerType;


@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.aad")
@ConditionalOnProperty("spring.security.oauth2.resourceserver.aad.issuer-uri")
public class AzureAdResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

}