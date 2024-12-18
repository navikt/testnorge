package no.nav.testnav.libs.reactivesecurity.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.NavAzureAdTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.azuread.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

@AutoConfiguration(after = ClientCredentialAutoConfiguration.class)
public class TokenServiceAutoConfiguration {

    @Value("${http.proxy:#{null}}")
    private String proxyHost;

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean(AzureAdTokenService.class)
    AzureAdTokenService azureAdTokenService(
            @Value("${AAD_ISSUER_URI:#{null}}") String issuerUrl,
            AzureClientCredential clientCredential,
            GetAuthenticatedToken getAuthenticatedToken
    ) {
        Assert.notNull(issuerUrl, "AAD_ISSUER_URI must be set");
        return new AzureAdTokenService(proxyHost, issuerUrl, clientCredential, getAuthenticatedToken);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean(NavAzureAdTokenService.class)
    NavAzureAdTokenService azureNavTokenService(
            AzureNavClientCredential azureNavClientCredential
    ) {
        return new NavAzureAdTokenService(proxyHost, azureNavClientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean(TrygdeetatenAzureAdTokenService.class)
    TrygdeetatenAzureAdTokenService trygdeetatenAzureAdTokenService(
            AzureTrygdeetatenClientCredential clientCredential,
            GetAuthenticatedUserId getAuthenticatedUserId,
            ObjectMapper objectMapper
    ) {
        return new TrygdeetatenAzureAdTokenService(proxyHost, clientCredential, getAuthenticatedUserId, objectMapper);
    }

}
