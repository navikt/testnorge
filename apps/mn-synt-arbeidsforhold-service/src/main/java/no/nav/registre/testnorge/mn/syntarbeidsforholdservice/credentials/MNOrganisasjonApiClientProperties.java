package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.domain.ClientCredential;

@Getter
@Configuration
public class MNOrganisasjonApiClientProperties {
    private final String baseUrl;
    private final String clientId;

    public MNOrganisasjonApiClientProperties(
            @Value("${consumers.mnorganiasjonapi.client_id}") String clientId,
            @Value("${consumers.mnorganiasjonapi.url}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
    }
}
