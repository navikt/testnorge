package no.nav.registre.orkestratoren.consumer.credential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class PersonApiClientCredential extends ClientCredential {
    public PersonApiClientCredential(
            @Value("${consumers.person.client_id}") String clientId,
            @Value("${consumers.person.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
