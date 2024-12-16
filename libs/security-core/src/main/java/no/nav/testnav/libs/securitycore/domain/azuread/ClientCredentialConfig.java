package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.*;
import org.springframework.util.Assert;

@Configuration
public class ClientCredentialConfig {

    private static final String AZURE_MISSING = "AZURE_APP_CLIENT_ID and AZURE_APP_CLIENT_SECRET must be set";
    private static final String TRYGDEETATEN_MISSING = "AZURE_TRYGDEETATEN_APP_CLIENT_ID and AZURE_TRYGDEETATEN_APP_CLIENT_SECRET must be set";
    private static final String PROXY_MISSING = "AZURE_NAV_APP_CLIENT_ID and AZURE_NAV_APP_CLIENT_SECRET must be set";

    private static final String TEST_TOKEN_ENDPOINT = "test-token-endpoint";
    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_SECRET = "test-client-secret";

    @Value("${AZURE_APP_CLIENT_ID:#{null}}")
    private String azureClientId;

    @Value("${AZURE_APP_CLIENT_SECRET:#{null}}")
    private String azureClientSecret;

    @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_ID:#{null}}")
    private String azureTrygdeetatenClientId;

    @Value("${AZURE_TRYGDEETATEN_APP_CLIENT_SECRET:#{null}}")
    private String azureTrygdeetatenClientSecret;

    @Value("${AZURE_NAV_APP_CLIENT_ID:#{null}}")
    private String azureNavClientId;

    @Value("${AZURE_NAV_APP_CLIENT_SECRET:#{null}}")
    private String azureNavClientSecret;

    @Bean("azureNavClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureNavClientCredential.class)
    public AzureNavClientCredential azureNavClientCredential() {
        Assert.hasLength(azureClientId, AZURE_MISSING);
        Assert.hasLength(azureClientSecret, AZURE_MISSING);
        return new AzureNavClientCredential(azureClientId, azureClientSecret);
    }

    @Bean("azureNavClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureNavClientCredential.class)
    public AzureNavClientCredential azureNavClientCredentialTest() {
        return new AzureNavClientCredential(TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean("azureTrygdeetatenClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureTrygdeetatenClientCredential.class)
    @ConditionalOnProperty("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT")
    public AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredential(
            @Value("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT") String trygdeetatenTokenEndpoint
    ) {
        Assert.hasLength(azureTrygdeetatenClientId, TRYGDEETATEN_MISSING);
        Assert.hasLength(azureTrygdeetatenClientSecret, TRYGDEETATEN_MISSING);
        return new AzureTrygdeetatenClientCredential(trygdeetatenTokenEndpoint, azureTrygdeetatenClientId, azureTrygdeetatenClientSecret);
    }

    @Bean("azureTrygdeetatenClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureTrygdeetatenClientCredential.class)
    public AzureTrygdeetatenClientCredential azureTrygdeetatenClientCredentialTest() {
        return new AzureTrygdeetatenClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

    @Bean("azureNavProxyClientCredential")
    @Profile("!test")
    @ConditionalOnMissingBean(AzureNavProxyClientCredential.class)
    @ConditionalOnProperty("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT")
    public AzureNavProxyClientCredential azureNavProxyClientCredential(
            @Value("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT") String azureNavTokenEndpoint
    ) {
        Assert.hasLength(azureNavClientId, PROXY_MISSING);
        Assert.hasLength(azureNavClientSecret, PROXY_MISSING);
        return new AzureNavProxyClientCredential(azureNavTokenEndpoint, azureNavClientId, azureNavClientSecret);
    }

    @Bean("azureNavProxyClientCredential")
    @Profile("test")
    @ConditionalOnMissingBean(AzureNavProxyClientCredential.class)
    public AzureNavProxyClientCredential azureNavProxyClientCredentialTest() {
        return new AzureNavProxyClientCredential(TEST_TOKEN_ENDPOINT, TEST_CLIENT_ID, TEST_CLIENT_SECRET);
    }

}
