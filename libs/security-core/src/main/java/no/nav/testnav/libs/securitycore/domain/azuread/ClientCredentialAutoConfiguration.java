package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@AutoConfiguration
public class ClientCredentialAutoConfiguration {

    private static final String TEST_TOKEN_ENDPOINT = "test-token-endpoint";
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";

    @Primary
    @Bean
    @Profile("test")
    AzureClientCredential azureClientCredentialTest() {
        return new AzureClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForAzure
    @ConditionalOnMissingBean(AzureClientCredential.class)
    AzureClientCredential azureClientCredential(
            @Value("${AZURE_OPENID_CONFIG_TOKEN_ENDPOINT}") String azureTokenEndpoint,
            @Value("${AZURE_APP_CLIENT_ID}") String azureClientId,
            @Value("${AZURE_APP_CLIENT_SECRET}") String azureClientSecret
    ) {
        return new AzureClientCredential(azureTokenEndpoint, azureClientId, azureClientSecret);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredentialTest() {
        return new AzureTrygdeetatenClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForTrygdeetaten
    @ConditionalOnMissingBean(AzureTrygdeetatenClientCredential.class)
    AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential(
            @Value("${AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT}") String azureTrygdeetatenTokenEndpoint,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_ID}") String azureTrygdeetatenClientId,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_SECRET}") String azureTrygdeetatenClientSecret
    ) {
        return new AzureTrygdeetatenClientCredential(azureTrygdeetatenTokenEndpoint, azureTrygdeetatenClientId, azureTrygdeetatenClientSecret);
    }

    @Primary
    @Bean
    @Profile("test")
    AzureNavClientCredential azureNavClientCredentialTest() {
        return new AzureNavClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean
    @ConditionalOnDollyApplicationConfiguredForNav
    @ConditionalOnMissingBean(AzureNavClientCredential.class)
    AzureNavClientCredential azureNavClientCredential(
            @Value("${AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT}") String azureNavTokenEndpoint,
            @Value("${AZURE_NAV_APP_CLIENT_ID}") String azureNavClientId,
            @Value("${AZURE_NAV_APP_CLIENT_SECRET}") String azureNavClientSecret
    ) {
        return new AzureNavClientCredential(azureNavTokenEndpoint, azureNavClientId, azureNavClientSecret);
    }

}
