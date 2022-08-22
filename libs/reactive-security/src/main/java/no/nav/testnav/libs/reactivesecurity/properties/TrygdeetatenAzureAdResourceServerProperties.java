package no.nav.testnav.libs.reactivesecurity.properties;

import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.trygdeetaten")
@ConditionalOnProperty("spring.security.oauth2.resourceserver.trygdeetaten.issuer-uri")
public class TrygdeetatenAzureAdResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.AZURE_AD;
    }

}
