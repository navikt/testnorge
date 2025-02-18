package no.nav.testnav.libs.reactivesecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.securitycore.domain.azuread.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@AutoConfiguration(after = ClientCredentialAutoConfiguration.class)
public class TokenServiceAutoConfiguration {

    @Value("${HTTP_PROXY:#{null}}")
    private String httpProxy;

    @Primary
    @Bean
    @Profile("test")
    AzureTokenService azureAdTokenServiceTest(
            AzureClientCredential clientCredential
    ) {
        return new AzureTokenService.Test(clientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean(AzureTokenService.class)
    AzureTokenService azureAdTokenService(
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        return new AzureTokenService(httpProxy, clientCredential, getAuthenticatedToken);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureNavTokenService azureNavTokenServiceTest(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new AzureNavTokenService.Test(azureNavClientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean(AzureNavTokenService.class)
    AzureNavTokenService azureNavTokenService(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new AzureNavTokenService(httpProxy, azureNavClientCredential);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenServiceTest(
            AzureTrygdeetatenClientCredential clientCredential
    ) {
        return new AzureTrygdeetatenTokenService.Test(clientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean(AzureTrygdeetatenTokenService.class)
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenService(
            AzureTrygdeetatenClientCredential clientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper
    ) {
        return new AzureTrygdeetatenTokenService(httpProxy, clientCredential, getAuthenticatedUserId, objectMapper);
    }

}
