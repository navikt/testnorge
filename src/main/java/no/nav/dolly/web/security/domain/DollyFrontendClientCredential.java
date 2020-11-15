package no.nav.dolly.web.security.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DollyFrontendClientCredential extends ClientCredential {
    public DollyFrontendClientCredential(
            @Value("${azure.app.client.id}") String clientId,
            @Value("${azure.app.client.secret}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
