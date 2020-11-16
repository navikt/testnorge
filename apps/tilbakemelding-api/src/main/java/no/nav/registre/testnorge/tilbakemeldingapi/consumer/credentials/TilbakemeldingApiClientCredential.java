package no.nav.registre.testnorge.tilbakemeldingapi.consumer.credentials;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class TilbakemeldingApiClientCredential extends ClientCredential {
    public TilbakemeldingApiClientCredential(
            @Value("${azure.app.client.id}") String clientId,
            @Value("${azure.app.client.secret}") String clientSecret
    ) {
        super(clientId, clientSecret);
    }
}
