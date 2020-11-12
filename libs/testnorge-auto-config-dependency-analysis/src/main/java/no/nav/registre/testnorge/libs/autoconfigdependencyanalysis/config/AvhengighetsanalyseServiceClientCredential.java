package no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Configuration
public class AvhengighetsanalyseServiceClientCredential extends ClientCredential {
    private static final String CLIENT_ID = "a90b186a-6896-4a79-9462-03b8cc9c36a9";

    public AvhengighetsanalyseServiceClientCredential(@Value("${AVHENGIGHETSANALYSE_SERVICE_CLIENT_SECRET}") String clientSecret) {
        super(CLIENT_ID, clientSecret);
    }

    public String getHost() {
        return "https://testnorge-avhengighetsanalyse-service.dev.adeo.no";
    }
}
