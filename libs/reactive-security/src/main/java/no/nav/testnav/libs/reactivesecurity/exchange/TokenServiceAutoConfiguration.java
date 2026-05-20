package no.nav.testnav.libs.reactivesecurity.exchange;

import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@AutoConfiguration(after = ClientCredentialAutoConfiguration.class)
@RequiredArgsConstructor
public class TokenServiceAutoConfiguration {

    @Value("${HTTP_PROXY:#{null}}")
    private String httpProxy;

    private final WebClient webClient;
    private final GetAuthenticatedToken getAuthenticatedToken;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final ObjectMapper objectMapper;

    @Primary
    @Bean
    @Profile("test")
    AzureTokenService azureAdTokenServiceTest(AzureClientCredential clientCredential) {
        return new AzureTokenService.Test(webClient, null, clientCredential, getAuthenticatedToken);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean(AzureTokenService.class)
    AzureTokenService azureAdTokenService(AzureClientCredential clientCredential) {
        return new AzureTokenService(webClient, httpProxy, clientCredential, getAuthenticatedToken);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureNavTokenService azureNavTokenServiceTest(AzureNavClientCredential clientCredential) {
        return new AzureNavTokenService.Test(webClient, null, clientCredential);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean(AzureNavTokenService.class)
    AzureNavTokenService azureNavTokenService(AzureNavClientCredential clientCredential) {
        return new AzureNavTokenService(webClient, httpProxy, clientCredential);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenServiceTest(AzureTrygdeetatenClientCredential clientCredential) {
        return new AzureTrygdeetatenTokenService.Test(webClient, null, clientCredential, getAuthenticatedUserId, objectMapper);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean(AzureTrygdeetatenTokenService.class)
    AzureTrygdeetatenTokenService trygdeetatenAzureAdTokenService(AzureTrygdeetatenClientCredential clientCredential) {
        return new AzureTrygdeetatenTokenService(webClient, httpProxy, clientCredential, getAuthenticatedUserId, objectMapper);
    }

}
