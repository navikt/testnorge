package no.nav.dolly.web.security.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DollyFrontendClientCredential extends ClientCredential {
    public DollyFrontendClientCredential(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${CLIENT_SECRET}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
