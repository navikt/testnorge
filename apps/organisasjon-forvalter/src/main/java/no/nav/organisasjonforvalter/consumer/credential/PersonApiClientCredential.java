package no.nav.organisasjonforvalter.consumer.credential;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonApiClientCredential extends ClientCredential {
    public PersonApiClientCredential(
            @Value("${client.id}") String clientId,
            @Value("${client.secret}") String clientSecret) {
        super(clientId, clientSecret);
    }
}
