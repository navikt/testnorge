package no.nav.registre.testnorge.tilbakemeldingapi.consumer.credentials;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TilbakemeldingApiClientCredential extends ClientCredential {
    public TilbakemeldingApiClientCredential(
            @Value("${CLIENT_ID}") String clientId,
            @Value("${CLIENT_SECRET}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
