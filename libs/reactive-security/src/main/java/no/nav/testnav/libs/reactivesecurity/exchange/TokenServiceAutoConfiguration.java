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
import org.springframework.util.Assert;

@AutoConfiguration(after = ClientCredentialAutoConfiguration.class)
public class TokenServiceAutoConfiguration {

    @Value("${http.proxy:#{null}}")
    private String proxyHost;

    @Primary
    @Bean
    @Profile("test")
    AzureTokenService azureAdTokenServiceTest(
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        return new AzureTokenService(null, null, clientCredential, getAuthenticatedToken);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean(AzureTokenService.class)
    AzureTokenService azureAdTokenService(
            @Value("${AAD_ISSUER_URI:#{null}}") String issuerUrl,
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        Assert.notNull(issuerUrl, "AAD_ISSUER_URI must be set");
        return new AzureTokenService(proxyHost, issuerUrl, clientCredential, getAuthenticatedToken);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureNavTokenService azureNavTokenServiceTest(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new AzureNavTokenService(null, azureNavClientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean(AzureNavTokenService.class)
    AzureNavTokenService azureNavTokenService(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new AzureNavTokenService(proxyHost, azureNavClientCredential);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenServiceTest(
            AzureTrygdeetatenClientCredential clientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper
    ) {
        return new AzureTrygdeetatenTokenService(null, clientCredential, getAuthenticatedUserId, objectMapper);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean(AzureTrygdeetatenTokenService.class)
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenService(
            AzureTrygdeetatenClientCredential clientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper
    ) {
        return new AzureTrygdeetatenTokenService(proxyHost, clientCredential, getAuthenticatedUserId, objectMapper);
    }

}
