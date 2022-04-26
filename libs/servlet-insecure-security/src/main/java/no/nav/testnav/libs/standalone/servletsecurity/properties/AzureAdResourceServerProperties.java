package no.nav.testnav.libs.standalone.servletsecurity.properties;

import no.nav.testnav.libs.standalone.servletsecurity.domain.ResourceServerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.aad")
@ConditionalOnProperty("spring.security.oauth2.resourceserver.aad.issuer-uri")
public class AzureAdResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

}