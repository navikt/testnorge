package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.credentials;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class ArbeidsforholdApiClientProperties {
    private final String baseUrl;
    private final String clientId;

    public ArbeidsforholdApiClientProperties(
            @Value("${consumers.arbeidsforholdapi.client_id}") String clientId,
            @Value("${consumers.arbeidsforholdapi.url}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
    }
}
