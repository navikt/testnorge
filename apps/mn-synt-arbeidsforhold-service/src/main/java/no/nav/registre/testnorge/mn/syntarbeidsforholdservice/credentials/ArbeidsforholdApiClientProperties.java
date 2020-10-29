package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class ArbeidsforholdApiClientProperties extends ClientCredential {
    private final String baseUrl;

    public ArbeidsforholdApiClientProperties(
            @Value("${consumers.arbeidsforholdapi.client_id}") String clientId,
            @Value("${consumers.arbeidsforholdapi.client_secret}") String clientSecret,
            @Value("${consumers.arbeidsforholdapi.url}") String baseUrl
    ) {
        super(clientId, clientSecret);
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
