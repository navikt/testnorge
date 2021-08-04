package no.nav.registre.testnorge.synt.person.consumer.credential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.servletsecurity.domain.ClientCredential;

@Configuration
public class PersonApiClientCredential extends ClientCredential {
    public PersonApiClientCredential(
            @Value("${consumers.personapi.client_id}") String clientId,
            @Value("${consumers.personapi.client_secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
