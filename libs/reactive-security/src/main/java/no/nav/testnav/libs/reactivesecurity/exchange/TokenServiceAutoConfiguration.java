package no.nav.testnav.libs.reactivesecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.azuread.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class TokenServiceAutoConfiguration {

    @Value("${HTTP_PROXY:#{null}}")
    private String httpProxy;

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean
    AzureTokenService azureAdTokenService(
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        return new AzureTokenService(httpProxy, clientCredential, getAuthenticatedToken);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean
    AzureNavTokenService azureNavTokenService(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new AzureNavTokenService(httpProxy, azureNavClientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenService(
            AzureTrygdeetatenClientCredential clientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper
    ) {
        return new AzureTrygdeetatenTokenService(httpProxy, clientCredential, getAuthenticatedUserId, objectMapper);
    }

}
