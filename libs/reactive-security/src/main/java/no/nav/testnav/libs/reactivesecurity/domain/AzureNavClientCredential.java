package no.nav.testnav.libs.reactivesecurity.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureNavClientCredential extends ClientCredential {

    public AzureNavClientCredential(
            @Value("${azure.app.client.id:#{null}}") String clientId,
            @Value("${azure.app.client.secret:#{null}}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
