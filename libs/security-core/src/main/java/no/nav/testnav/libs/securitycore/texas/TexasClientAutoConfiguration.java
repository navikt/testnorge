package no.nav.testnav.libs.securitycore.texas;

import no.nav.testnav.libs.securitycore.domain.azuread.AzureClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@AutoConfiguration
@ConditionalOnExpression("'${dolly.texas.url.token:${NAIS_TOKEN_ENDPOINT:}}' != ''")
public class TexasClientAutoConfiguration {

    @Value("${dolly.texas.url.token:${NAIS_TOKEN_ENDPOINT}}")
    private String tokenEndpoint;

    @Value("${dolly.texas.url.exchange:${NAIS_TOKEN_EXCHANGE_ENDPOINT}}")
    private String exchangeEndpoint;

    @Value("${dolly.texas.local.secret:${DOLLY_TEXAS_PROXY_SHARED_SECRET:#{null}}}")
    private String localSecret;

    @Bean
    @Profile({"local", "localdb"})
    @ConditionalOnMissingBean
    TexasClient localTexasClient(
            AzureClientCredential clientCredential,
            @Value("${AZURE_OPENID_CONFIG_ISSUER}") String entraIdIssuer,
            @Value("${TOKEN_X_ISSUER}") String tokenXIssuer,
            @Value("${TOKEN_X_CLIENT_ID:#{null}}") String tokenXClientId
    ) {
        return new LocalTexasClient(tokenEndpoint, exchangeEndpoint, localSecret, clientCredential, entraIdIssuer, tokenXIssuer, tokenXClientId);
    }

    @Bean
    @Profile("!local & !localdb")
    @ConditionalOnMissingBean
    TexasClient texasClient(@Value("${dolly.texas.url.introspect:${NAIS_TOKEN_INTROSPECTION_ENDPOINT}}") String introspectionEndpoint) {
        return new TexasClient(tokenEndpoint, exchangeEndpoint, introspectionEndpoint, localSecret);
    }
}
