package no.nav.registre.testnorge.synt.sykemelding.consumer.credential;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class HelsepersonellApiClientCredential extends ClientCredential {
    public HelsepersonellApiClientCredential(
            @Value("${consumers.helsepersonell.client_id}") String clientId,
            @Value("${consumers.helsepersonell.client_secret}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
