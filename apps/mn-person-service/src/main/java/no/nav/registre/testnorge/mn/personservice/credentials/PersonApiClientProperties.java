package no.nav.registre.testnorge.mn.personservice.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.domain.ClientCredential;

@Configuration
public class PersonApiClientProperties extends ClientCredential {
    private final Integer threads;
    private final String baseUrl;

    public PersonApiClientProperties(
            @Value("${consumers.personapi.client_id}") String clientId,
            @Value("${consumers.personapi.client_secret}") String clientSecret,
            @Value("${consumers.personapi.threads}") Integer threads,
            @Value("${consumers.personapi.url}") String baseUrl
    ) {
        super(clientId, clientSecret);
        this.threads = threads;
        this.baseUrl = baseUrl;
    }

    public Integer getThreads() {
        return threads;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
