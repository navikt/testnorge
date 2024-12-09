package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Get configuration from, in prioritized order:
 * <ol>
 *     <li>{@code AZURE_APP_CLIENT_[ID|SECRET]} (provided by NAIS when running in pod)</li>
 *     <li>{@code spring.security.oauth2.client.registration.aad.client-[id|secret]} (configured when running locally)</li>
 *     <li>{@code null} (for test purposes)</li>
 * </ol>
 */
@Configuration
public class AzureNavClientCredential extends ClientCredential {

    public AzureNavClientCredential(
            @Value("#{systemProperties['AZURE_APP_CLIENT_ID'] ?: '${spring.security.oauth2.client.registration.aad.client-id:#{null}}'}") String clientId,
            @Value("#{systemProperties['AZURE_APP_CLIENT_SECRET'] ?: '${spring.security.oauth2.client.registration.aad.client-secret:#{null}}'}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }

}
