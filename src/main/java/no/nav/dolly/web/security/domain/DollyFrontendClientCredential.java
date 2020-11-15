package no.nav.dolly.web.security.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DollyFrontendClientCredential extends ClientCredential {
    public DollyFrontendClientCredential(
            @Value("${spring.security.oauth2.client.registration.aad.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.aad.client-secret}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
