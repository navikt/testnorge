package no.nav.testnav.apps.tilgangservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AltinnConfig {

    private final String url;
    private final String apiKey;
    private final String code;
    private final String edition;

    public AltinnConfig(
            @Value("${altinn.api.url}") String url,
            @Value("${altinn.api.key}") String apiKey,
            @Value("${altinn.service.code}") String code,
            @Value("${altinn.service.edition}") String edition
    ) {
        this.url = url;
        this.apiKey = apiKey;
        this.code = code;
        this.edition = edition;
    }

    public String getCode() {
        return code;
    }

    public String getEdition() {
        return edition;
    }

    public String getUrl() {
        return url;
    }

    public String getApiKey() {
        return apiKey;
    }
}
