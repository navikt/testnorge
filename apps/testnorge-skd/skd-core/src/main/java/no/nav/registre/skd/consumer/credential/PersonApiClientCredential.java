package no.nav.registre.skd.consumer.credential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.domain.ClientCredential;

@Configuration
public class PersonApiClientCredential extends ClientCredential {
    public PersonApiClientCredential(
            @Value("${person.rest.api.client_id}") String clientId,
            @Value("${person.rest.api.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
