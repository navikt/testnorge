package no.nav.testnav.libs.reactivesecurity.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Auto configuration for resource server properties, e.g. configuration under {@code spring.security.oauth2.resourceserver}.
 * Supported properties are:
 * <ul>
 *     <li>{@code spring.security.oauth2.resourceserver.aad}</li>
 *     <li>{@code spring.security.oauth2.resourceserver.tokenx}</li>
 *     <li>{@code spring.security.oauth2.resourceserver.trygdeetaten}</li>
 * </ul>
 * which may each have the following properties:
 * <ul>
 *     <li>{@code issuer-uri}</li>
 *     <li>{@code accepted-audience}</li>
 * </ul>
 */
@AutoConfiguration
public class ResourceServerPropertiesAutoConfiguration {

    @Bean
    @ConditionalOnProperty({
            "spring.security.oauth2.resourceserver.aad.issuer-uri",
            "spring.security.oauth2.resourceserver.aad.accepted-audience"
    })
    @ConditionalOnMissingBean
    AzureAdResourceServerProperties azureAdResourceServerProperties(
            @Value("${spring.security.oauth2.resourceserver.aad.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.aad.accepted-audience}") List<String> acceptedAudience
    ) {
        return new AzureAdResourceServerProperties(issuerUri, acceptedAudience);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    AzureAdResourceServerProperties azureAdResourceServerPropertiesTest(
    ) {
        return null;
    }

    @Bean
    @ConditionalOnProperty({
            "spring.security.oauth2.resourceserver.tokenx.issuer-uri",
            "spring.security.oauth2.resourceserver.tokenx.accepted-audience"
    })
    @ConditionalOnMissingBean
    TokenxResourceServerProperties tokenxResourceServerProperties(
            @Value("${spring.security.oauth2.resourceserver.tokenx.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.tokenx.accepted-audience}") List<String> acceptedAudience
    ) {
        return new TokenxResourceServerProperties(issuerUri, acceptedAudience);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    TokenxResourceServerProperties tokenxResourceServerPropertiesTest(
    ) {
        return null;
    }

    @Bean
    @ConditionalOnProperty({
            "spring.security.oauth2.resourceserver.trygdeetaten.issuer-uri",
            "spring.security.oauth2.resourceserver.trygdeetaten.accepted-audience"
    })
    @ConditionalOnMissingBean
    TrygdeetatenAzureAdResourceServerProperties trygdeetatenAzureAdResourceServerProperties(
            @Value("${spring.security.oauth2.resourceserver.trygdeetaten.issuer-uri}") String issuerUri,
            @Value("${spring.security.oauth2.resourceserver.trygdeetaten.accepted-audience}") List<String> acceptedAudience
    ) {
        return new TrygdeetatenAzureAdResourceServerProperties(issuerUri, acceptedAudience);
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean
    TrygdeetatenAzureAdResourceServerProperties trygdeetatenAzureAdResourceServerPropertiesTest(
    ) {
        return null;
    }

}
