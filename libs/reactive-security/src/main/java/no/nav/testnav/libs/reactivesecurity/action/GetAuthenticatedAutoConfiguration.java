package no.nav.testnav.libs.reactivesecurity.action;

import no.nav.testnav.libs.reactivesecurity.properties.AzureAdResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.ResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TokenxResourceServerProperties;
import no.nav.testnav.libs.reactivesecurity.properties.TrygdeetatenAzureAdResourceServerProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import java.util.ArrayList;

@AutoConfiguration
public class GetAuthenticatedAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    GetAuthenticatedResourceServerType getAuthenticatedResourceServerType(
            @Nullable AzureAdResourceServerProperties azureAdResourceServerProperties,
            @Nullable TokenxResourceServerProperties tokenxResourceServerProperties,
            @Nullable TrygdeetatenAzureAdResourceServerProperties trygdeetatenAzureAdResourceServerProperties
    ) {
        var list = new ArrayList<ResourceServerProperties>(3);
        if (azureAdResourceServerProperties != null) {
            list.add(azureAdResourceServerProperties);
        }
        if (tokenxResourceServerProperties != null) {
            list.add(tokenxResourceServerProperties);
        }
        if (trygdeetatenAzureAdResourceServerProperties != null) {
            list.add(trygdeetatenAzureAdResourceServerProperties);
        }
        return new GetAuthenticatedResourceServerType(list);
    }

    @Bean
    @ConditionalOnMissingBean
    GetAuthenticatedToken getAuthenticatedToken(GetAuthenticatedResourceServerType getAuthenticatedResourceServerType) {
        return new GetAuthenticatedToken(getAuthenticatedResourceServerType);
    }

}
