package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureNavClientCredential extends ClientCredential {

    /*
    TODO: A better solution, for another day:
        1. No longer import AzureNavClientCredential - generify this, or use factories. It cannot be a @Configuration for its own @Bean.
        2. Create two beans, one for @Profile("test"), one on @ConditionalOnMissingBean.
        3. Check if we really need subclasses for this, or if ClientCredential will suffice. Check all other extends ClientCredential.
        This class is effectively both a @Configuration and a @Bean with immutable config, which doesn't play nice.
     */
    public AzureNavClientCredential(
            @Value("#{systemProperties['spring.profiles.active'] == 'test' ? 'test-client-id' : '${AZURE_APP_CLIENT_ID:#{null}}'}") String clientId,
            @Value("#{systemProperties['spring.profiles.active'] == 'test' ? 'test-client-secret' : '${AZURE_APP_CLIENT_SECRET:#{null}}'}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }

}
