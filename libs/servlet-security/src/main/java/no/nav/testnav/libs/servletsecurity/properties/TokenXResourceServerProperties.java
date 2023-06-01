package no.nav.testnav.libs.servletsecurity.properties;


import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.tokenx")
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
public class TokenXResourceServerProperties extends ResourceServerProperties {

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }
}