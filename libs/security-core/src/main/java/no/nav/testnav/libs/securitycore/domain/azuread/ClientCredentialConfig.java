package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

@Configuration
public class ClientCredentialConfig {

    private static final String AZURE_MISSING = "AAD_ISSUER_URI, AZURE_APP_CLIENT_ID and AZURE_APP_CLIENT_SECRET must be set";
    private static final String TRYGDEETATEN_MISSING = "AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT, AZURE_TRYGDEETATEN_APP_CLIENT_ID and AZURE_TRYGDEETATEN_APP_CLIENT_SECRET must be set";
    private static final String NAV_MISSING = "AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT, AZURE_NAV_APP_CLIENT_ID and AZURE_NAV_APP_CLIENT_SECRET must be set";

    private static final String TEST_TOKEN_ENDPOINT = "test-token-endpoint";
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";

    @Bean("azureClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureClientCredential.class)
    @ConditionalOnProperty("AAD_ISSUER_URI")
    public AzureClientCredential azureClientCredential(
            @Value("${AAD_ISSUER_URI:#{null}}") String azureTokenEndpoint, // TODO: Not currently used, AAD_ISSUER_URI is hardcoded elsewhere; should be refactored to use AZURE_OPENID_CONFIG_TOKEN_ENDPOINT instead.
            @Value("${AZURE_APP_CLIENT_ID:#{null}}") String azureClientId,
            @Value("${AZURE_APP_CLIENT_SECRET:#{null}}") String azureClientSecret
    ) {
        Assert.hasLength(azureTokenEndpoint, AZURE_MISSING);
        Assert.hasLength(azureClientId, AZURE_MISSING);
        Assert.hasLength(azureClientSecret, AZURE_MISSING);
        return new AzureClientCredential(azureTokenEndpoint, azureClientId, azureClientSecret);
    }

    @Bean("azureClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureClientCredential.class)
    public AzureClientCredential azureClientCredentialTest() {
        return new AzureClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean("azureTrygdeetatenClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureTrygdeetatenClientCredential.class)
    @ConditionalOnProperty("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT")
    public AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential(
            @Value("${AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT:#{null}}") String azureTrygdeetatenTokenEndpoint,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_ID:#{null}}") String azureTrygdeetatenClientId,
            @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_SECRET:#{null}}") String azureTrygdeetatenClientSecret
    ) {
        Assert.hasLength(azureTrygdeetatenTokenEndpoint, TRYGDEETATEN_MISSING);
        Assert.hasLength(azureTrygdeetatenClientId, TRYGDEETATEN_MISSING);
        Assert.hasLength(azureTrygdeetatenClientSecret, TRYGDEETATEN_MISSING);
        return new AzureTrygdeetatenClientCredential(azureTrygdeetatenTokenEndpoint, azureTrygdeetatenClientId, azureTrygdeetatenClientSecret);
    }

    @Bean("azureTrygdeetatenClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureTrygdeetatenClientCredential.class)
    public AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredentialTest() {
        return new AzureTrygdeetatenClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean("azureNavClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureNavClientCredential.class)
    @ConditionalOnProperty("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT")
    public AzureNavClientCredential azureNavClientCredential(
            @Value("${AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT:#{null}}") String azureNavTokenEndpoint,
            @Value("${AZURE_NAV_APP_CLIENT_ID:#{null}}") String azureNavClientId,
            @Value("${AZURE_NAV_APP_CLIENT_SECRET:#{null}}") String azureNavClientSecret
    ) {
        Assert.hasLength(azureNavTokenEndpoint, NAV_MISSING);
        Assert.hasLength(azureNavClientId, NAV_MISSING);
        Assert.hasLength(azureNavClientSecret, NAV_MISSING);
        return new AzureNavClientCredential(azureNavTokenEndpoint, azureNavClientId, azureNavClientSecret);
    }

    @Bean("azureNavClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureNavClientCredential.class)
    public AzureNavClientCredential azureNavClientCredentialTest() {
        return new AzureNavClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

}
